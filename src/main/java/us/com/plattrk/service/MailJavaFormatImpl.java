package us.com.plattrk.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import us.com.plattrk.api.model.ChronComparator;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.api.model.ProductComparator;
import us.com.plattrk.repository.IncidentChronologyRepository;
import us.com.plattrk.service.Mail.Type;

public class MailJavaFormatImpl implements MailFormat {
	
	@Autowired
	private IncidentChronologyRepository incidentChronologyRepository;
	
	@Autowired
	private Properties appProperties;

	private Incident incident;

	private StringBuilder productsString = new StringBuilder();

	private LinkedList<IncidentChronology> chronologiesSortedByStartTime = new LinkedList<IncidentChronology>();

	private String testmsg = ""; // used to put special message indicator i.e. TEST MSG
	
	public MailJavaFormatImpl(){
	}
	
	public void initialize(Incident incident) {
		
		StringBuilder productsStringTemp = new StringBuilder();
		List<Product> products = new ArrayList<>();
		
		testmsg = appProperties.getProperty("SUBJECTMSG", "");

		this.setIncident(incident);
		products.addAll(incident.getProducts());
		Collections.sort(products, new ProductComparator());
		
		for (int i = 0; i < products.size(); i++) {	
			if (i < (products.size() - 1)) {
				productsStringTemp.append(products.get(i).getIncidentName() + ", ");
			}
			else {
				productsStringTemp.append(products.get(i).getIncidentName());
			}
		}
		
		productsString = productsStringTemp;
		
		chronologiesSortedByStartTime.clear();
		chronologiesSortedByStartTime.addAll(incidentChronologyRepository.getChronologiesPerIncident(incident.getId()));
		Collections.sort(chronologiesSortedByStartTime, new ChronComparator());
	}

	@Override
	public String generateBodyFormat(boolean isReport) {
		
		String body = "";
		boolean multipleProducts = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		if (incident.getProducts().size() > 1) {
			multipleProducts = true;
		}
		
		if (isReport) {
			body = "<div>Impacted Product" + (multipleProducts ? "s: ": ": ") 
					+ "<div><span style=\"background-color: #FFFF00\">" 
					+ productsString.toString() 
					+ "</span></div><br>" 
					+ "<div>Start Time: " + dateFormat.format(incident.getStartTime()) + "</div><br>"
					+ "<div>End Time: " + (incident.getEndTime() != null ? dateFormat.format(incident.getEndTime()) : "") + "</div><br>";
			
		}
		else 
		{
			body = "<div>The following product" + (multipleProducts ? "s ": " ") + (multipleProducts ? "are ": "is ")
					+ (incident.getStatus().equals("Closed") ? "no longer experiencing an issue:":  "experiencing an issue:")
					+ "</div><br>"
					+ "<div><span style=\"background-color: #FFFF00\">" + productsString.toString() + "</span></div><br>"
					+ "<div>Date / Time: " + dateFormat.format(new Date()) + "</div><br>"
					+ "<div>Reported By: " + normalizeNameString(incident.getRecordedBy()) + "</div><br>"
					+ "<div>For any questions please call 860-271-7640.</div><br>"
					+ "<div>Start Time: " + dateFormat.format(incident.getStartTime()) + "</div><br>"
					+ "<div>End Time: " + (incident.getEndTime() != null ? dateFormat.format(incident.getEndTime()) : "") + "</div><br>";
		}
		
		body += "<div>Severity: <span style=\"background-color: #FFFF00\"><b>" 
				+ (incident.getSeverity().equals("P1") ? "HIGH": "") 
				+ (incident.getSeverity().equals("P2") ? "MEDIUM": "") 
				+ (incident.getSeverity().equals("P3") ? "LOW": "") 
				+ "</span></b></div><br>"
				+ "<div>System Status: <span style=\"background-color: #FFFF00\"><b>" 
				+ ( incident.getStatus().equals("Closed") ? "UP" : incident.getApplicationStatus().getDisplayName()) 
				+ "</span></b></div><br>"
				+ "<div>Description: " + incident.getDescription() + "</div><br>"
				+ (incident.getSummary() != null ? "<div>Business Impact: " + incident.getSummary() + "</div><br>": " ");
		
		if (!chronologiesSortedByStartTime.isEmpty()) {
				
			if (incident.getEndTime() != null) {	
				
				boolean specifyFirstInstance = false; 
				
				for (IncidentChronology chronology: chronologiesSortedByStartTime) {
					if (clearSecondsFromDate(chronology.getDateTime()).getTime() <= clearSecondsFromDate(incident.getEndTime()).getTime()) {
						body += "<div>Update: " + dateFormat.format(chronology.getDateTime()) + " - " + chronology.getDescription() + "</div><br>";
					} 
					else if (!specifyFirstInstance) 
						 {
							 specifyFirstInstance = true;
							 if (incident.getCorrectiveAction() != null) {
						        body += "<div>Resolution: " + incident.getCorrectiveAction() + "</div><br>";
							 }
							 body += "<div>Update: " + dateFormat.format(chronology.getDateTime()) + " - " + chronology.getDescription() + "</div><br>";	
						 } 
					     else 
					     {
					    	 body += "<div>Update: " + dateFormat.format(chronology.getDateTime()) + " - " + chronology.getDescription() + "</div><br>";
					     }
				}
				
			}
			else
			{  
				for (IncidentChronology chronology: chronologiesSortedByStartTime) { 
					body += "<div>Update: " + dateFormat.format(chronology.getDateTime()) + " - " + chronology.getDescription() + "</div><br>";	
				}
				body += (incident.getCorrectiveAction() != null ? "<div>Resolution: " + incident.getCorrectiveAction(): "") + "</div><br>";
			}		
		}
		else 
		{
			body += (incident.getCorrectiveAction() != null ? "<div>Resolution: " + incident.getCorrectiveAction(): "") + "</div><br>";
		}
		
		body += (incident.getCallsReceived() != 0 ? "<div>Client services received " + incident.getCallsReceived() + " calls during this outage.</div><br>": " ");
		
		return body;
	}
	
	private Date clearSecondsFromDate(Date date) {
		
		Calendar instance = Calendar.getInstance();
		instance.setTime(date);
		instance.clear(Calendar.SECOND);
		return instance.getTime();	
	}

	private String normalizeNameString(String recordedBy) {
			
		int positionOfPeriod = recordedBy.indexOf('.');
		String firstName = recordedBy.substring(0, positionOfPeriod);
		firstName = Character.toUpperCase(firstName.charAt(0)) + firstName.substring(1);
		String lastName = recordedBy.substring(positionOfPeriod+1);
		lastName = Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1);
		return firstName + " " + lastName;
	}

	@Override
	public String generateSubjectFormat(Type type) {
		
		String subject = testmsg;
		
		switch(type) {
			case INCIDENTSTART:
					subject += "New problem impacting " + productsString.toString(); 
					break;
			case INCIDENTUPDATE:
					subject += "Update to problem impacting " + productsString.toString();
					break;
			case INCIDENT55NOUPDATE:
			case INCIDENT1HNOUPDATE:
					subject += "No update to problem impacting " + productsString.toString(); 
					break;
			case INCIDENT2HNOUPDATE:
					subject += "Escalate no update to problem impacting " + productsString.toString(); 
					break;
			case INCIDENTCHRONOLOGYSTART:
					subject += "Update to problem impacting " + productsString.toString(); 
					break;
			case INCIDENTEND:
					subject += "Resolved problem impacting " + productsString.toString(); 
					break;
			case INCIDENTCREATEEND:
					subject += "New Problem/Resolved impacting " + productsString.toString(); 
					break;
		default:
			return subject;
		}
		return subject + " ";
	
	}
	
	public void setIncident(Incident incident) {
		this.incident = incident;
	}

}
