package us.com.plattrk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Notification;
import us.com.plattrk.api.model.Type;
import us.com.plattrk.repository.IncidentRepository;
import us.com.plattrk.repository.NotificationRepository;

import javax.mail.SendFailedException;
import java.time.LocalDateTime;
import java.util.Properties;

public class IncidentNotificationServiceImpl implements IncidentNotificationService {

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

    @Override
    public boolean earlyAlert() {
        if (this.incident == null)
            throw new IllegalStateException("No Incident set.");

        boolean sentAlert = false;
//        int earlyAlertInSeconds = Integer.valueOf(appProperties.getProperty("EarlyAlertInSeconds", "3300"));
        int earlyAlertInSeconds = 300;

        Notification notification = getNotification();
        if (notification != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime plusSecs;

            if (notification.getLastEarlyAlertDateTime() != null) {
                LocalDateTime lastEarlyAlertDateTime = notification.getLastEarlyAlertDateTime();
                plusSecs = lastEarlyAlertDateTime.plusSeconds(earlyAlertInSeconds);
            } else {
                LocalDateTime startDateTime = notification.getStartDateTime();
                plusSecs = startDateTime.plusSeconds(earlyAlertInSeconds);
            }

            if (now.isAfter(plusSecs)) {
                try {
                    sendEmail(Mail.Type.INCIDENT55NOUPDATE);
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
//        int alertInSecondsOffset = Integer.valueOf(appProperties.getProperty("AlertInSecondsOffset", "300"));
        int alertInSecondsOffset = 600;

        Notification notification = getNotification();
        if (notification != null) {
            if (notification.getStartDateTime() == null)
                throw new IllegalStateException("Incident notification start date time not set.");
            if (notification.getLastEarlyAlertDateTime() == null)
                throw new IllegalStateException("Incident notification last early alert date time not set");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime plusSecs = notification.getLastEarlyAlertDateTime().plusSeconds(alertInSecondsOffset);

            if (now.isAfter(plusSecs)) {
                try {
                    sendEmail(Mail.Type.INCIDENT1HNOUPDATE);
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
//        int escalatedAlertInSeconds = Integer.valueOf(appProperties.getProperty("EscalatedAlertInSeconds", "300"));
        int escalatedAlertInSeconds = 900;

        Notification notification = getNotification();
        if (notification != null) {
            if (notification.getStartDateTime() == null)
                throw new IllegalStateException("Incident notification start date time not set.");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime plusSecs;
            LocalDateTime lastEscalatedAlertDateTime = notification.getLastEscalatedAlertDateTime();
            if (lastEscalatedAlertDateTime != null) {
                plusSecs = lastEscalatedAlertDateTime.plusSeconds(escalatedAlertInSeconds);
            } else {
                plusSecs = notification.getStartDateTime().plusSeconds(escalatedAlertInSeconds);
            }

            if (now.isAfter(plusSecs)) {
                try {
                    sendEmail(Mail.Type.INCIDENT2HNOUPDATE);
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
    public void sendEmail(Mail.Type type) throws SendFailedException {
        // send email notification for new chronology and retrieve latest incident to see if any updates have occurred.
        incident = incidentRepository.getIncident(incident.getId()).get();
        try {
            mailService.send(incident, appProperties, type);
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
        return notificationRepository.getNotification(Type.INCIDENT.name(), incident.getId());
    }

}
