package us.com.plattrk.service;

import us.com.plattrk.api.model.Incident;

import java.util.Properties;

public interface IncidentNotificationLegacyService {

    public void run();

    public void sendEmail(Mail.Type type);

    public void setIncident(Incident incident);

    public void setAppProperties(Properties appProperties);

    public void setMailService(MailService mailService);

}
