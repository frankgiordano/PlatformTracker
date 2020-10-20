package us.com.plattrk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import us.com.plattrk.api.model.EntityType;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Notification;
import us.com.plattrk.repository.IncidentRepository;
import us.com.plattrk.repository.NotificationRepository;
import us.com.plattrk.service.Mail.Type;

import javax.mail.SendFailedException;
import java.time.LocalDateTime;
import java.util.*;

public class IncidentNotificationServiceImpl extends NotificationTimeFrame implements IncidentNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentNotificationServiceImpl.class);

    // this is wired via xml configuration to allow us to easily switch between socket and java mail implementations.
    private MailService mailService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private Properties appProperties;

    @Autowired
    private IncidentRepository incidentRepository;

    private Incident incident;

    public IncidentNotificationServiceImpl() {
        super(true);
        if (isOnHours() && isWeekDay()) {
            checkWeekDayAfterHours();
        }
        if (isOnHours() && isWeekEnd()) {
            checkWeekEndAfterHours();
        }
    }

    @Override
    public boolean resetAlert() {
        if (this.incident == null)
            throw new IllegalStateException("No Incident set.");

        int numOfIncidentChronologies = incident.getChronologies().size();
        if (numOfIncidentChronologies == 0)
            return false;

        Notification notification = getNotification();
        int numOfNotificationChronologies = notification.getNumOfChronologies();

        if (numOfIncidentChronologies == numOfNotificationChronologies)
            return false;

        LocalDateTime now = LocalDateTime.now();
        notification.setLastAlertOffSetDateTime(now);
        notification.setLastEarlyAlertDateTime(now);
        notification.setLastEscalatedAlertDateTime(now);
        notification.setNumOfChronologies(numOfIncidentChronologies);
        notificationRepository.save(notification);

        return true;
    }

    @Override
    public boolean earlyAlert() {
        if (this.incident == null)
            throw new IllegalStateException("No Incident set.");

        boolean sentAlert = false;
        int earlyAlertInSecs = Integer.valueOf(appProperties.getProperty("EarlyAlertInSeconds", "3300"));

        Notification notification = getNotification();
        if (notification != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime plusSecs;

            if (notification.getLastEarlyAlertDateTime() != null) {
                LocalDateTime lastEarlyAlertDateTime = notification.getLastEarlyAlertDateTime();
                plusSecs = lastEarlyAlertDateTime.plusSeconds(earlyAlertInSecs);
            } else {
                LocalDateTime startDateTime = notification.getStartDateTime();
                plusSecs = startDateTime.plusSeconds(earlyAlertInSecs);
            }

            if (now.isAfter(plusSecs)) {
                try {
                    sendEmail(Type.INCIDENT55NOUPDATE);
                } catch (SendFailedException e) {
                    LOG.error("IncidentNotificationServiceImpl::earlyAlert - error sending email notification", e);
                    return false;
                }
                sentAlert = true;
                notification.setLastEarlyAlertDateTime(now);
                notificationRepository.save(notification);
            }
        }
        return sentAlert;
    }

    @Override
    public boolean alertOffSet() {
        if (this.incident == null)
            throw new IllegalStateException("No Incident set.");

        boolean sentAlert = false;
        int earlyAlertInSecs = Integer.valueOf(appProperties.getProperty("EarlyAlertInSeconds", "3300"));
        int alertOffSetInSecs = Integer.valueOf(appProperties.getProperty("AlertInSecondsOffset", "300"));

        if (alertOffSetInSecs > earlyAlertInSecs)
            throw new IllegalStateException("alertInSecondsOffset should be less than earlyAlertInSeconds");

        Notification notification = getNotification();
        if (notification != null) {
            if (notification.getStartDateTime() == null)
                throw new IllegalStateException("Incident notification start date time not set.");
            if (notification.getLastEarlyAlertDateTime() == null)
                throw new IllegalStateException("Incident notification last early alert date time not set.");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime plusSecs;
            LocalDateTime lastAlertOffSetDateTime = notification.getLastAlertOffSetDateTime();
            if (lastAlertOffSetDateTime == null) {
                plusSecs = notification.getLastEarlyAlertDateTime().plusSeconds(alertOffSetInSecs);
            } else {
                LocalDateTime lastEarlyAlertDateTime = notification.getLastEarlyAlertDateTime();
                if (lastAlertOffSetDateTime.isAfter(lastEarlyAlertDateTime)
                        || lastAlertOffSetDateTime.isEqual(lastEarlyAlertDateTime)) {
                    return false;
                }
                plusSecs = lastEarlyAlertDateTime.plusSeconds(alertOffSetInSecs);
            }

            if (now.isAfter(plusSecs)) {
                try {
                    sendEmail(Type.INCIDENT1HNOUPDATE);
                } catch (SendFailedException e) {
                    LOG.error("IncidentNotificationServiceImpl::alertOffSet - error sending email notification ", e);
                    return false;
                }
                sentAlert = true;
                notification.setLastAlertOffSetDateTime(now);
                notificationRepository.save(notification);
            }
        }
        return sentAlert;
    }

    @Override
    public boolean escalatedAlert() {
        if (this.incident == null)
            throw new IllegalStateException("No Incident set.");

        boolean sentAlert = false;
        int escalatedAlertInSecs = Integer.valueOf(appProperties.getProperty("EscalatedAlertInSeconds", "6600"));
        int earlyAlertInSecs = Integer.valueOf(appProperties.getProperty("EarlyAlertInSeconds", "3300"));

        if (escalatedAlertInSecs < earlyAlertInSecs)
            throw new IllegalStateException("escalatedAlertInSeconds should be more than earlyAlertInSeconds");

        Notification notification = getNotification();
        if (notification != null) {
            if (notification.getStartDateTime() == null)
                throw new IllegalStateException("Incident notification start date time not set.");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime plusSecs;
            LocalDateTime lastEscalatedAlertDateTime = notification.getLastEscalatedAlertDateTime();
            if (lastEscalatedAlertDateTime != null) {
                plusSecs = lastEscalatedAlertDateTime.plusSeconds(escalatedAlertInSecs);
            } else {
                plusSecs = notification.getStartDateTime().plusSeconds(escalatedAlertInSecs);
            }

            if (now.isAfter(plusSecs)) {
                try {
                    sendEmail(Type.INCIDENT2HNOUPDATE);
                } catch (SendFailedException e) {
                    LOG.error("IncidentNotificationServiceImpl::escalatedAlert - error sending email notification ", e);
                    return false;
                }
                sentAlert = true;
                notification.setLastEscalatedAlertDateTime(now);
                notificationRepository.save(notification);
            }
        }
        return sentAlert;
    }

    @Override
    public void sendEmail(Type type) throws SendFailedException {
        incident = incidentRepository.getIncident(incident.getId()).get();
        try {
            if (isOnHours()) {
                mailService.send(incident, appProperties, type);
            }
        } catch (SendFailedException e) {
            throw e;
        }
    }

    @Override
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    private Notification getNotification() {
        return notificationRepository.getNotification(EntityType.INCIDENT.name(), incident.getId());
    }

}
