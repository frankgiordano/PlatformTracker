package us.com.plattrk.service;

import java.util.Properties;

import us.com.plattrk.api.model.EmailAddress;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.service.Mail.Type;

import javax.mail.SendFailedException;

public interface MailService {

    public void send(Incident incident, Properties appProperties, Type type) throws SendFailedException;

    public void sendDailyReport(Properties appProperties, String body, String subject);

    public boolean sendWeeklyReport(Properties appProperties, String body, String file, String fileName, String subject, EmailAddress address);

}
