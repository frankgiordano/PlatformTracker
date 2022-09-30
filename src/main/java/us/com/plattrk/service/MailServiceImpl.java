package us.com.plattrk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.com.plattrk.api.model.EmailAddress;
import us.com.plattrk.api.model.Incident;

import javax.mail.SendFailedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MailServiceImpl implements MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

    // this is wired via xml configuration to allow us to easily switch between
    // socket and java mail implementations.
    private Mail mail;

    public MailServiceImpl() {
    }

    public MailServiceImpl(Mail mail) {
        setMail(mail);
    }

    @Override
    public void send(Incident incident, Properties appProperties, Mail.Type type) throws SendFailedException {
        try {
            mail.setIncident(incident);
            mail.setProperties(appProperties);
            mail.setType(type);
            mail.setFileName(""); // don't send any attachment
            mail.generateEmailString();
            mail.send();
        } catch (SendFailedException e) {
            LOG.error("MailServiceImpl::send - error sending email service layer - {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void sendDailyReport(Properties appProperties, String body, String subject) {
        try {
            List<String> allEmailAddresses = new ArrayList<>(Arrays.asList(appProperties.getProperty("Outages").split(",")));
            mail.setAllEmailAddresses(allEmailAddresses);
            mail.setSubject(subject);
            mail.setBody(body);
            mail.setFileName(""); // don't send any attachment
            mail.setProperties(appProperties);
            mail.send();
        } catch (Exception e) {
            LOG.error("MailServiceImpl::sendDailyReport - error sending sendDailyReport email service layer {}", e.getMessage());
        }
    }

    @Override
    public boolean sendWeeklyReport(Properties appProperties, String body, String file, String fileName, String subject,
            EmailAddress address) {
        try {
            List<String> allEmailAddresses = new ArrayList<>();
            if (address != null) {
                allEmailAddresses.addAll(Arrays.asList(address.getAddress()));
            } else {
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty("Weekly Prod").split(",")));
            }
            mail.setAllEmailAddresses(allEmailAddresses);
            mail.setSubject(subject);
            mail.setBody(body);
            mail.setProperties(appProperties);
            mail.setFileName(fileName); // send with an attachment
            mail.setFile(file);
            mail.send();
        } catch (Exception e) {
            LOG.error("MailServiceImpl::sendWeeklyReport - error sending sendWeeklyReport email service layer {}", e.getMessage());
        }
        return true;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

}
