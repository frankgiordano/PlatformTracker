package us.com.plattrk.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;

public class MailJavaImpl implements Mail {

    private static Logger log = LoggerFactory.getLogger(MailJavaImpl.class);

    private Type type;
    private Incident incident;
    private Properties appProperties;
    private List<String> allEmailAddresses = new ArrayList<String>();
    private String subject;
    private String body;
    private DataSource source;
    private String fileName;
    private String file;

    // this is wired via xml configuration to allow us to easily switch between
    // socket and java mail implementations.
    MailFormat mailFormat;

    public MailJavaImpl() {
    }

    public MailJavaImpl(MailFormat mailFormat) {
        this.mailFormat = mailFormat;
    }

    @Override
    public void generateEmailString() {

        // get the product state(s) associated with the incident
        Set<Product> products = incident.getProducts();
        // append all product email distribution addresses together
        allEmailAddresses = generateEmailProductDistroList(products);
        mailFormat.initialize(incident);

        // log.info("in generateEmailString, incoming incident.Receipents = " +
        // incident.getEmailRecipents());
        // log.info("in generateEmailString, incoming appProperties.Receipents = " +
        // appProperties.getProperty(incident.getEmailRecipents()));

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
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty("RTS Desktop Operations Only").split(",")));
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

        // guard against a problem when null are inserted in allEmailAddresses, problem
        // with Arrays.asList() I believe
        for (int i = 0; i < allEmailAddresses.size(); i++) {
            // log.info("inspect email array " + i + " " + allEmailAddresses.get(i));
            if (allEmailAddresses.get(i) == null) {
                allEmailAddresses.remove(i);
            }
        }
        return;
    }

    @Override
    public List<String> generateEmailProductDistroList(Set<Product> products) {
        List<String> productEmailDistroArray = new ArrayList<String>();
        for (Product product : products) {
            productEmailDistroArray.add(appProperties.getProperty(product.getShortName()));
        }
        return productEmailDistroArray;
    }

    @Override
    public String getAllEmailAddresses(List<String> allEmailAddresses) {
        StringBuilder buffer = null;
        Iterator<String> iterator = allEmailAddresses.iterator();
        if (allEmailAddresses.size() > 1) {
            buffer = new StringBuilder(iterator.next());
            while (iterator.hasNext()) {
                buffer.append(",").append(iterator.next());
            }
            return buffer.toString();
        } else {
            return allEmailAddresses.get(0);
        }
    }

    @Override
    public void send() throws SendFailedException {

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", appProperties.getProperty("Host", "nhp-outlook.plattrk.inc"));
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(appProperties.getProperty("From", "plattrk-support@plattrk.com")));

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
            log.info("email sent = " + message.toString());

        } catch (MessagingException mex) {
            mex.printStackTrace();
            throw new SendFailedException();
        }
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

    public void setMailFormat(MailFormat mailFormat) {
        this.mailFormat = mailFormat;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void setFile(String file) {
        this.file = file;
    }

}
