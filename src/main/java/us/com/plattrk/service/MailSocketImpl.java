package us.com.plattrk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.util.MailUtil;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

public class MailSocketImpl implements Mail {

    private static final Logger LOG = LoggerFactory.getLogger(MailSocketImpl.class);

    @Autowired
    private MailUtil mailUtil;

    private Type type;
    private Incident incident;
    private Properties appProperties;
    private String output;
    private List<String> allEmailAddresses = new ArrayList<>();

    @Override
    public void send() {
        try {
            Socket socket = new Socket("hostname", 9999);
            PrintStream out = new PrintStream(socket.getOutputStream());

            // Send an email notification. A python socket listener setup on server. Drop a ^
            // separated string on port xxxx on hostname in this format Email@emailaddress.com^subject^body
            out.print(output);
            out.flush();
            LOG.info("MailSocketImpl::send - email sent {}" + output);

            socket.close();
        } catch (IOException e) {
            LOG.error("MailSocketImpl::send - email during incident create failed to send {}", e.getMessage());
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

        switch (type) {
            case INCIDENTSTART:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                output = getAllEmailAddresses(allEmailAddresses) + "^" + incident.getTag() + " Platform Tracker Incident created. " + appProperties.getProperty("Site") + "^" + incident.getDescription();
                break;
            case INCIDENTUPDATE:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                output = getAllEmailAddresses(allEmailAddresses) + "^" + incident.getTag() + " Platform Tracker Incident updated. " + appProperties.getProperty("Site") + "^" + incident.getDescription();
                break;
            case INCIDENT55NOUPDATE:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty("Desktop Operations Only").split(",")));
                output = getAllEmailAddresses(allEmailAddresses) + "^" + incident.getTag() + " Platform Tracker Incident no status change please update. " + appProperties.getProperty("Site") + "^" + incident.getDescription();
                break;
            case INCIDENT1HNOUPDATE:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                output = getAllEmailAddresses(allEmailAddresses) + "^" + incident.getTag() + " Platform Tracker Incident no status change please update. " + appProperties.getProperty("Site") + "^" + incident.getDescription();
                break;
            case INCIDENT2HNOUPDATE:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty("Escalated Distribution").split(",")));
                output = getAllEmailAddresses(allEmailAddresses) + "^" + incident.getTag() + " Platform Tracker Incident no status escalate. " + appProperties.getProperty("Site") + "^" + incident.getDescription();
                break;
            case INCIDENTCHRONOLOGYSTART:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                output = getAllEmailAddresses(allEmailAddresses) + "^" + incident.getTag() + " Platform Tracker Incident chronology created. " + appProperties.getProperty("Site") + "^" + incident.getDescription();
                break;
            case INCIDENTEND:
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty(incident.getEmailRecipents()).split(",")));
                output = getAllEmailAddresses(allEmailAddresses) + "^" + incident.getTag() + " Platform Tracker Incident - closed. " + appProperties.getProperty("Site") + "^" + incident.getDescription();
                break;
            default:
                output = null;
        }
    }

    @Override
    public List<String> generateEmailProductDistroList(Set<Product> products) {
        List<String> productEmailDistroArray = new ArrayList<>();
        for (Product product : products) {
            productEmailDistroArray.add(appProperties.getProperty(product.getShortName()));
        }
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
    }

    @Override
    public void setSubject(String subject) {
    }

    @Override
    public void setAllEmailAddresses(List<String> allEmailAddresses) {
    }

    @Override
    public void setFileName(String fileName) {
    }

    @Override
    public void setFile(String file) {
    }

}
