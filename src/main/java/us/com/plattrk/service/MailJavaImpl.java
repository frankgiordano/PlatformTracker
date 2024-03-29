package us.com.plattrk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.util.MailUtil;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.*;

public class MailJavaImpl implements Mail {

    private static final Logger LOG = LoggerFactory.getLogger(MailJavaImpl.class);

    @Autowired
    private MailUtil mailUtil;

    private Type type;
    private Incident incident;
    private Properties appProperties;
    private List<String> allEmailAddresses = new ArrayList<>();
    private String subject;
    private String body;
    private DataSource source;
    private String fileName;
    private String file;

    // this is wired via xml configuration to allow us to easily switch between
    // socket and java mail implementations.
    private MailFormat mailFormat;

    public MailJavaImpl() {
    }

    public MailJavaImpl(MailFormat mailFormat) {
        setMailFormat(mailFormat);
    }

    @Override
    public void send() throws SendFailedException {

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", appProperties.getProperty("Host", "platformtracker"));
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(appProperties.getProperty("From", "platformtracker-support@platformtracker.com")));

            InternetAddress[] address = new InternetAddress[allEmailAddresses.size()];
            for (int i = 0; i < allEmailAddresses.size(); i++) {
                address[i] = new InternetAddress(((String) allEmailAddresses.get(i)));
            }

            message.addRecipients(Message.RecipientType.TO, address);
            message.setSubject(subject);

            if (!fileName.isEmpty()) {
                // if fileName is not empty then attach file to the email
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(body);
                Multipart multiPart = new MimeMultipart();
                multiPart.addBodyPart(messageBodyPart);

                messageBodyPart = new MimeBodyPart();
                source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(fileName);
                multiPart.addBodyPart(messageBodyPart);
                message.setContent(multiPart);
            } else {
                message.setContent(body, "text/html");
            }

            Transport.send(message);
            LOG.info("MailJavaImpl::send - email sent {}", message);

        } catch (MessagingException e) {
            LOG.error("MailJavaImpl::send - sending email failure {}", e.getMessage());
            throw new SendFailedException();
        }
    }

    @Override
    public String getAllEmailAddresses(List<String> allEmailAddresses) {
        return mailUtil.getAllEmailAddresses(allEmailAddresses);
    }

    @Override
    public void generateEmailString() {

        // get the product state(s) associated with the incident
        Set<Product> products = incident.getProducts();
        // append all product email distribution addresses together
        allEmailAddresses = generateEmailProductDistroList(products);
        mailFormat.initialize(incident);

        LOG.debug("MailJavaImpl::generateEmailString - incoming incident recipients {}", incident.getEmailRecipents());
        LOG.debug("MailJavaImpl::generateEmailString - incoming appProperties recipients {}", appProperties.getProperty(incident.getEmailRecipents()));

        switch (type) {
            case INCIDENTSTART:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                subject = mailFormat.generateSubjectFormat(Type.INCIDENTSTART);
                body = mailFormat.generateBodyFormat(false);
                break;
            case INCIDENTUPDATE:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                subject = mailFormat.generateSubjectFormat(Type.INCIDENTUPDATE);
                body = mailFormat.generateBodyFormat(false);
                break;
            case INCIDENT55NOUPDATE:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty("Desktop Operations Only").split(",")));
                subject = mailFormat.generateSubjectFormat(Type.INCIDENT55NOUPDATE);
                body = mailFormat.generateBodyFormat(false);
                break;
            case INCIDENT1HNOUPDATE:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                subject = mailFormat.generateSubjectFormat(Type.INCIDENT1HNOUPDATE);
                body = mailFormat.generateBodyFormat(false);
                break;
            case INCIDENT2HNOUPDATE:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty("Escalated Distribution").split(",")));
                subject = mailFormat.generateSubjectFormat(Type.INCIDENT2HNOUPDATE);
                body = mailFormat.generateBodyFormat(false);
                break;
            case INCIDENTCHRONOLOGYSTART:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                subject = mailFormat.generateSubjectFormat(Type.INCIDENTCHRONOLOGYSTART);
                body = mailFormat.generateBodyFormat(false);
                break;
            case INCIDENTEND:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                subject = mailFormat.generateSubjectFormat(Type.INCIDENTEND);
                body = mailFormat.generateBodyFormat(false);
                break;
            case INCIDENTCREATEEND:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                subject = mailFormat.generateSubjectFormat(Type.INCIDENTCREATEEND);
                body = mailFormat.generateBodyFormat(false);
                break;
            default:
                subject = null;
                body = null;
                return;
        }

        // guard against a problem when null are inserted in allEmailAddresses, problem with Arrays.asList() I believe
        for (int i = 0; i < allEmailAddresses.size(); i++) {
            LOG.debug("MailJavaImpl::generateEmailString - inspect email array {}: {}", i, allEmailAddresses.get(i));
            if (allEmailAddresses.get(i) == null) {
                allEmailAddresses.remove(i);
            }
        }
    }

    @Override
    public List<String> generateEmailProductDistroList(Set<Product> products) {
        List<String> productEmailDistroArray = new ArrayList<>();
        products.forEach(i -> {
            Optional<String> prop = Optional.ofNullable(appProperties.getProperty(i.getShortName()));
            prop.ifPresent((productEmailDistroArray::add));
        });
        return productEmailDistroArray;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    @Override
    public void setProperties(Properties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public void setAllEmailAddresses(List<String> allEmailAddresses) {
        this.allEmailAddresses = allEmailAddresses;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void setFile(String file) {
        this.file = file;
    }

    public void setMailFormat(MailFormat mailFormat) {
        this.mailFormat = mailFormat;
    }

}
