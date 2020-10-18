package us.com.plattrk.service;

import us.com.plattrk.api.model.Incident;

public interface IncidentNotificationService {

    public boolean earlyAlert(Incident incident);

    public boolean alertOffSet(Incident incident);

    public boolean escalatedAlert(Incident incident);

    public void sendEmail(Mail.Type type, Incident incident);

    public void setMailService(MailService mailService);

}
