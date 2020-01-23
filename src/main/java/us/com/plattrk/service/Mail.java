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
	
	void send() throws SendFailedException;
	
	String getAllEmailAddresses(List<String> allEmailAddresses);
	
	List<String> generateEmailProductDistroList(Set<Product> products);
	
	void generateEmailString();

	void setType(Type type);

	void setIncident(Incident incident);

	void setProperties(Properties appProperties);
	
	void setBody(String body);

	void setSubject(String subject);
	
	void setAllEmailAddresses(List<String> allEmailAddresses);

	void setFileName(String fileName);

	void setFile(String file);

}
