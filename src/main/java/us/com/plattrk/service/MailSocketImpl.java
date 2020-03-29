package us.com.plattrk.service;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;

public class MailSocketImpl implements Mail {

    private static Logger log = LoggerFactory.getLogger(IncidentServiceImpl.class);

    private Type type;
    private Incident incident;
    private Properties appProperties;
    private String output;
    private List<String> allEmailAddresses = new ArrayList<String>();

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
                allEmailAddresses.addAll(Arrays.asList(appProperties.getProperty("RTS Desktop Operations Only").split(",")));
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
                return;
        }

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
    public void send() {
        try {
            Socket socket = new Socket("ebola.gssolrs.net", 8404);
            PrintStream out = new PrintStream(socket.getOutputStream());

            // Send an email notification. Use the python socket listener setup by Steve Eaton. Drop a ^ 
            // separated string on port 8404 on ebola.gssolrs.net in this format Email@emailaddress.com^subject^body

            out.print(output);
            out.flush();
            log.info("email sent = " + output);

            socket.close();
        } catch (UnknownHostException e) {
            log.error("Email during incident create failed to send.");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Email during incident create failed to send.");
            e.printStackTrace();
        }

        return;
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
        // TODO Auto-generated method stub

    }

    @Override
    public void setSubject(String subject) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAllEmailAddresses(List<String> allEmailAddresses) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFileName(String fileName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFile(String file) {
        // TODO Auto-generated method stub

    }

}
