package us.com.plattrk.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.com.plattrk.api.model.EmailAddress;
import us.com.plattrk.api.model.Incident;

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
    public void send(Incident incident, Properties appProperties, Mail.Type type) {
        try {
            mail.setIncident(incident);
            mail.setProperties(appProperties);
            mail.setType(type);
            mail.setFileName(""); // don't send any attachment
            mail.generateEmailString();
            mail.send();
        } catch (Exception e) {
            LOG.error("MailServiceImpl::send - Error sending email service layer.", e);
        }
    }

    @Override
    public void sendDailyReport(Properties appProperties, String body, String subject) {
        try {
            List<String> allEmailAddresses = new ArrayList<>();
            allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty("Outages").split(",")));
            mail.setAllEmailAddresses(allEmailAddresses);
            mail.setSubject(subject);
            mail.setBody(body);
            mail.setFileName(""); // don't send any attachment
            mail.setProperties(appProperties);
            mail.send();
        } catch (Exception e) {
            LOG.error("MailServiceImpl::sendDailyReport - Error sending sendDailyReport email service layer.", e);
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
            LOG.error("MailServiceImpl::sendWeeklyReport - Error sending sendWeeklyReport email service layer.", e);
        }
        return true;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

}
