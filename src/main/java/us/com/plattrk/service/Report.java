package us.com.plattrk.service;

import java.util.Date;
import java.util.List;

import us.com.plattrk.api.model.EmailAddress;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentReportByProduct;

public interface Report {
	
	void generateDailyReport(List<Incident> incidents, Date previousDayDate);

	void generateWeekEndReport(List<Incident> incidents, Date previousWeekEndDate, Date previousDayDate);

	boolean generateWeeklyReport(List<Incident> incidents, Date previousWeeklyDate, Date previousDayDate, EmailAddress address);

	boolean generateReportByProduct(List<Incident> incidents, IncidentReportByProduct incidentReport);
	
}
