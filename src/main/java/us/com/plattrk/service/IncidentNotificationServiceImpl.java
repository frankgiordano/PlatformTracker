package us.com.plattrk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Notification;
import us.com.plattrk.api.model.Type;
import us.com.plattrk.repository.IncidentRepository;
import us.com.plattrk.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.Properties;

@Service(value = "IncidentNotificationService")
public class IncidentNotificationServiceImpl {

    // this is wired via xml configuration to allow us to easily switch between socket and java mail implementations.
    private MailService mailService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private Properties appProperties;

    @Autowired
    private IncidentRepository incidentRepository;

    public boolean earlyAlert(Incident incident) {
        boolean sentAlert = false;
//        int earlyAlertInSeconds = Integer.valueOf(appProperties.getProperty("EarlyAlertInSeconds", "3300"));
        int earlyAlertInSeconds = 300;


        Notification notification = getNotification(incident);
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
                sendEmail(Mail.Type.INCIDENT55NOUPDATE, incident);
                sentAlert = true;
                notification.setLastEarlyAlertDateTime(now);
                notificationRepository.save(notification);
            }
        }
        return sentAlert;
    }

    public boolean alertOffSet(Incident incident) {
        boolean sentAlert = false;
//        int alertInSecondsOffset = Integer.valueOf(appProperties.getProperty("AlertInSecondsOffset", "300"));
        int alertInSecondsOffset = 600;

        Notification notification = getNotification(incident);
        if (notification != null) {
            if (notification.getStartDateTime() == null)
                throw new IllegalStateException("Incident notification start date time not set.");
            if (notification.getLastEarlyAlertDateTime() == null)
                throw new IllegalStateException("Incident notification last early alert date time not set");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime plusSecs = notification.getLastEarlyAlertDateTime().plusSeconds(alertInSecondsOffset);

            if (now.isAfter(plusSecs)) {
                sendEmail(Mail.Type.INCIDENT1HNOUPDATE, incident);
                sentAlert = true;
                notification.setLastAlertOffSetDateTime(now);
                notificationRepository.save(notification);
            }
        }
        return sentAlert;
    }

    public boolean escalatedAlert(Incident incident) {
        boolean sentAlert = false;
//        int escalatedAlertInSeconds = Integer.valueOf(appProperties.getProperty("EscalatedAlertInSeconds", "300"));
        int escalatedAlertInSeconds = 900;

        Notification notification = getNotification(incident);
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
                sendEmail(Mail.Type.INCIDENT2HNOUPDATE, incident);
                sentAlert = true;
                notification.setLastEscalatedAlertDateTime(now);
                notificationRepository.save(notification);
            }
        }
        return sentAlert;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    private Notification getNotification(Incident incident) {
        return notificationRepository.getNotification(Type.INCIDENT, incident.getId());
    }

    private void sendEmail(Mail.Type type, Incident incident) {
        // send email notification for new chronology and retrieve latest incident to see if any updates have occurred.
        incident = incidentRepository.getIncident(incident.getId()).get();
        mailService.send(incident, appProperties, type);
    }

}
