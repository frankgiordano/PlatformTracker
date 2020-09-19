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

public class IncidentNotificationService implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(IncidentNotificationService.class);

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
    private boolean onHours = true;

    // needed for spring container 
    public IncidentNotificationService() {
    }

    // this is for creation of thread without spring being involved. 
    public IncidentNotificationService(Incident incident, Properties appProperties) {
        this.incident = incident;
        this.appProperties = appProperties;
        this.earlyAlert = Long.valueOf(appProperties.getProperty("EarlyAlertInSeconds", "3300"));
        this.alert = Long.valueOf(appProperties.getProperty("AlertInSecondsOffset", "300"));
        this.escalatedAlert = Long.valueOf(appProperties.getProperty("EscalatedAlertInSeconds", "6600"));
        this.mailService = new MailServiceImpl(new MailJavaImpl(new MailJavaFormatImpl()));
    }

    @Override
    public void run() {
        log.info("Started NotificationThread name = " + Thread.currentThread().getName());

        boolean doOnceAfterRestart = false;

        // initialize Long Array list that grows due to meeting every x defined minutes
        // without an incident update
        List<Long> durations = new ArrayList<Long>();
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

                    log.info("found chronologies, in NotificationThread " + Thread.currentThread().getName());
                    List<IncidentChronology> mySortedChrons = new ArrayList<IncidentChronology>(incident.getChronologies());
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
                            log.info("startTime = " + startTime.getTime() + " chron date time = " + mySortedChrons.get(mySortedChrons.size() - 1).getDateTime().getTime());
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

                            sendEmail(Type.INCIDENTCHRONOLOGYSTART);
                        }
                    }
                } else {
                    log.info("no chronologies found, in NotificationThread " + Thread.currentThread().getName());
                }

                tEnd = System.currentTimeMillis();
                tDelta = tEnd - startTime.getTime();
                elapsedSeconds = tDelta / 1000.0;

                // get the total amount of duration to test with that if elapsedSeconds is greater to trigger email
                durationTotal = 0;
                for (Long i : durations) {
                    durationTotal += i;
                }

                log.info("elapsedSeconds " + elapsedSeconds + ", in NotificationThread " + Thread.currentThread().getName());
                log.info("early alert time = " + durationTotal + ", in NotificationThread " + Thread.currentThread().getName());

                // trigger early alert email
                if (elapsedSeconds > durationTotal) {
                    durations.add(earlyAlert);

                    // save previous duration reference for normal alert email
                    Long alertDurationTotal = 0L;
                    for (int i = 0; i < durations.size() - 1; i++) {
                        alertDurationTotal += durations.get(i);
                    }
                    alertDuration = alertDurationTotal + alert;

                    if (isOnHours()) {
                        sendEmail(Type.INCIDENT55NOUPDATE);
                    }
                }

                log.info("alert time = " + (alertDuration) + ", in NotificationThread " + Thread.currentThread().getName());
                log.info("escalated alert time = " + elapsedSeconds + " > " + escalatedAlert + ", in NotificationThread " + Thread.currentThread().getName());

                // normal alert - send escalated email if escalated alert time has passed.
                if (elapsedSeconds >= (alertDuration)) {
                    alertDuration = durationTotal + alert;
                    if (elapsedSeconds > escalatedAlert) {
                        if (isOnHours()) {
                            sendEmail(Type.INCIDENT2HNOUPDATE);
                        }
                    } else {
                        if (isOnHours()) {
                            sendEmail(Type.INCIDENT1HNOUPDATE);
                        }
                    }

                }
            } catch (InterruptedException e) {
                log.error("NotificationThread interrupted.");
                e.printStackTrace();
            }
        }

        if (!incidentRepository.isIncidentOpen(incident.getId())) {
            sendEmail(Type.INCIDENTEND);
        }

        log.info("Ended NotificationThread name = " + Thread.currentThread().getName());
    }

    public void sendEmail(Type type) {
        // send email notification for new chronology and retrieve latest incident to see if any updates have occurred.
        incident = incidentRepository.getIncident(incident.getId());
        mailService.send(incident, appProperties, type);
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    // this is called when we are starting the thread via spring container, the default constructor is used and we need to set the properties here
    // or we could have set the properties in application context xml bean definition for NotificationThread.
    public void setAppProperties(Properties appProperties) {
        this.appProperties = appProperties;
        this.earlyAlert = Long.valueOf(appProperties.getProperty("EarlyAlertInSeconds", "3300"));
        this.alert = Long.valueOf(appProperties.getProperty("AlertInSecondsOffset", "300"));
        this.escalatedAlert = Long.valueOf(appProperties.getProperty("EscalatedAlertInSeconds", "6600"));
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public boolean isWeekEnd() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return true;
        }

        return false;
    }

    public boolean isWeekDay() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.TUESDAY ||
                dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.THURSDAY
                || dayOfWeek == Calendar.FRIDAY) {
            return true;
        }

        return false;
    }

    public void checkWeekDayAfterHours() {
        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.HOUR_OF_DAY) >= 21) {
            setOnHours(false);
        }
    }

    public void checkWeekEndAfterHours() {
        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.HOUR_OF_DAY) >= 18) {
            setOnHours(false);
        }
    }

    public boolean isOnHours() {
        return onHours;
    }

    public void setOnHours(boolean onHours) {
        this.onHours = onHours;
    }

}