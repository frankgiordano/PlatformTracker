package us.com.plattrk.service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;
import us.com.plattrk.repository.IncidentChronologyRepository;
import us.com.plattrk.repository.IncidentRepository;
import us.com.plattrk.service.Mail.Type;

import javax.mail.SendFailedException;

public class IncidentNotificationLegacyServiceImpl extends NotificationTimeFrame implements Runnable, IncidentNotificationLegacyService {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentNotificationLegacyServiceImpl.class);

    // this is wired via xml configuration to allow us to easily switch between socket and java mail implementations.
    private MailService mailService;

    @Autowired
    private IncidentChronologyRepository incidentChronologyRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    private Incident incident;
    private Properties appProperties;

    private long earlyAlert;        // specified in seconds
    private long alert;             // specified in seconds
    private long escalatedAlert;    // specified in seconds

    private long durationTotal;
    private Date startTime;
    private long tEnd;
    private long tDelta;
    private double elapsedSeconds;
    private long alertDuration = 0L;
    private int numOfChronologies = 0;

    // needed for spring container 
    public IncidentNotificationLegacyServiceImpl() {
        super(true);
    }

    // this is for creation of thread without spring being involved. 
    public IncidentNotificationLegacyServiceImpl(Incident incident, Properties appProperties) {
        super(true);
        this.incident = incident;
        this.appProperties = appProperties;
        this.earlyAlert = Long.valueOf(appProperties.getProperty("EarlyAlertInSeconds", "3300"));
        this.alert = Long.valueOf(appProperties.getProperty("AlertInSecondsOffset", "300"));
        this.escalatedAlert = Long.valueOf(appProperties.getProperty("EscalatedAlertInSeconds", "6600"));
        this.mailService = new MailServiceImpl(new MailJavaImpl(new MailJavaFormatImpl()));
    }

    @Override
    public void run() {
        boolean doOnceAfterRestart = false;
        final String threadName = Thread.currentThread().getName();

        LOG.info("IncidentNotificationLegacyServiceImpl::run - started notification thread name {}", threadName);

        // initialize Long Array list that grows due to meeting every x defined minutes
        // without an incident update
        List<Long> durations = new ArrayList<>();
        durations.add(earlyAlert);
        alertDuration = earlyAlert + alert;
        startTime = new Date();

        // keep executing this thread until incident is closed..		
        while (incidentRepository.isIncidentOpen(incident.getId())) {

            if (isOnHours() && isWeekDay()) {
                checkWeekDayAfterHours();
            }

            if (isOnHours() && isWeekEnd()) {
                checkWeekEndAfterHours();
            }

            try {
                Thread.sleep(5000);

                // find if there are any existing chronologies - reach out to the DB and retrieve current set of chronologies and add them back 
                // to the incident instance - initial incident instance's chronologies may have changed hence the need to ping the DB each time
                incident.setChronologies(incidentChronologyRepository.getChronologiesPerIncident(incident.getId()));
                if (!incident.getChronologies().isEmpty()) {

                    LOG.info("IncidentNotificationLegacyServiceImpl::run - found chronologies in notification thread name {}", threadName);
                    List<IncidentChronology> mySortedChrons = new ArrayList<>(incident.getChronologies());
                    // find the last existing chronology by order to use its dateTime for calculation
                    mySortedChrons.sort(Comparator.comparing(IncidentChronology::getDateTime));

                    // store number of chronologies and check previous # and if changed reset for timer.  
                    if (mySortedChrons.size() > numOfChronologies) {

                        // if the current startTime when this notification thread was started is 
                        // greater than the latest chronology date\time, then the application was 
                        // somehow restarted due to a problem or normally for maintenance while an
                        // incident is still open; as such multiple update notifications in quick
                        // succession can be spawned and cause a major spam issue. 
                        // As a result, check that the startTime is greater then the latest
                        // chronology date, if so handle this situation differently 

                        if (!doOnceAfterRestart && startTime.getTime() >= mySortedChrons.get(mySortedChrons.size() - 1).getDateTime().getTime()) {
                            // reset the startTime to now
                            LOG.info("IncidentNotificationLegacyServiceImpl::run - startTime {}, chron date time {}", startTime.getTime(),
                                    mySortedChrons.get(mySortedChrons.size() - 1).getDateTime().getTime());
                            startTime = new Date();
                            numOfChronologies = mySortedChrons.size();
                            doOnceAfterRestart = true; // done once, don't come back into this code block until 
                            // application is restarted if any incident is still kept open
                        } else {
                            startTime = new Date(mySortedChrons.get(mySortedChrons.size() - 1).getDateTime().getTime());
                            durations.clear();
                            durations.add(earlyAlert);
                            alertDuration = earlyAlert + alert;
                            numOfChronologies = mySortedChrons.size();
                        }
                    }
                } else {
                    LOG.info("IncidentNotificationLegacyServiceImpl::run - no chronologies found in notification thread name {}", threadName);
                }

                tEnd = System.currentTimeMillis();
                tDelta = tEnd - startTime.getTime();
                elapsedSeconds = tDelta / 1000.0;

                // get the total amount of duration to test with that if elapsedSeconds is greater to trigger email
                durationTotal = 0;
                for (Long i : durations) {
                    durationTotal += i;
                }

                LOG.info("IncidentNotificationLegacyServiceImpl::run - elapsedSeconds {} in notification thread name {}", elapsedSeconds, threadName);
                LOG.info("IncidentNotificationLegacyServiceImpl::run - early alert time {} in notification thread name {}", durationTotal, threadName);

                // trigger early alert email
                if (elapsedSeconds > durationTotal) {
                    durations.add(earlyAlert);

                    // save previous duration reference for normal alert email
                    Long alertDurationTotal = 0L;
                    for (int i = 0; i < durations.size() - 1; i++) {
                        alertDurationTotal += durations.get(i);
                    }
                    alertDuration = alertDurationTotal + alert;

                    sendEmail(Type.INCIDENT55NOUPDATE);
                }

                LOG.info("IncidentNotificationLegacyServiceImpl::run - alert time {} in notification thread name {}", alertDuration, threadName);
                StringBuilder escalatedAlertTimeInfo = new StringBuilder();
                escalatedAlertTimeInfo.append("IncidentNotificationLegacyServiceImpl::run - escalated alert time ");
                escalatedAlertTimeInfo.append(elapsedSeconds);
                escalatedAlertTimeInfo.append(" > ");
                escalatedAlertTimeInfo.append(escalatedAlert);
                escalatedAlertTimeInfo.append(" in notification thread name ");
                escalatedAlertTimeInfo.append(threadName);
                LOG.info(escalatedAlertTimeInfo.toString());

                // normal alert - send escalated email if escalated alert time has passed.
                if (elapsedSeconds >= (alertDuration)) {
                    alertDuration = durationTotal + alert;
                    if (elapsedSeconds > escalatedAlert) {
                        sendEmail(Type.INCIDENT2HNOUPDATE);
                    } else {
                        sendEmail(Type.INCIDENT1HNOUPDATE);
                    }

                }
            } catch (InterruptedException e) {
                LOG.error("IncidentNotificationLegacyServiceImpl::run - notification thread name {} interrupted", threadName);
                e.printStackTrace();
            }
        }

        LOG.info("IncidentNotificationLegacyServiceImpl::run - ended notification thread name {}", threadName);
    }

    @Override
    public void sendEmail(Mail.Type type) {
        incident = incidentRepository.getIncident(incident.getId()).get();
        try {
            if (isOnHours()) {
                mailService.send(incident, appProperties, type);
            }
        } catch (SendFailedException e) {
            LOG.error("IncidentNotificationLegacyServiceImpl::sendEmail - error sending email notification ", e);
        }
    }

    @Override
    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    // this is called when we are starting the thread via spring container, the default constructor is used
    // and we need to set the properties here or we could have set the properties in application context xml
    // bean definition for NotificationThread.
    @Override
    public void setAppProperties(Properties appProperties) {
        this.appProperties = appProperties;
        this.earlyAlert = Long.valueOf(appProperties.getProperty("EarlyAlertInSeconds", "3300"));
        this.alert = Long.valueOf(appProperties.getProperty("AlertInSecondsOffset", "300"));
        this.escalatedAlert = Long.valueOf(appProperties.getProperty("EscalatedAlertInSeconds", "6600"));
    }

    @Override
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

}