package us.com.plattrk.service;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.SendFailedException;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;

public interface Mail {
	
	public enum Type {
		INCIDENTSTART, INCIDENTUPDATE, INCIDENT55NOUPDATE, INCIDENT1HNOUPDATE, INCIDENT2HNOUPDATE, INCIDENTCHRONOLOGYSTART, INCIDENTEND, INCIDENTCREATEEND
	}

	public void send() throws SendFailedException;

	public String getAllEmailAddresses(List<String> allEmailAddresses);

	public List<String> generateEmailProductDistroList(Set<Product> products);

	public void generateEmailString();

	public void setType(Type type);

	public void setIncident(Incident incident);

	public void setProperties(Properties appProperties);

	public void setBody(String body);

	public void setSubject(String subject);

	public void setAllEmailAddresses(List<String> allEmailAddresses);

	public void setFileName(String fileName);

	public void setFile(String file);

}
