package us.com.plattrk.service;

import us.com.plattrk.api.model.Incident;

import javax.mail.SendFailedException;

public interface IncidentNotificationService {

    public boolean resetAlert();

    public boolean earlyAlert();

    public boolean alertOffSet();

    public boolean escalatedAlert();

    public void sendEmail(Mail.Type type) throws SendFailedException;

    public void setMailService(MailService mailService);

    public void setIncident(Incident incident);

    public void toggleOnHours();

}
