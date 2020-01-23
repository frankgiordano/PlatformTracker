package us.com.plattrk.service;

import us.com.plattrk.api.model.EmailAddress;
import us.com.plattrk.api.model.ErrorCondition;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.api.model.IncidentReportByProduct;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.api.model.ReferenceData;
import us.com.plattrk.api.model.ToggleSwitch;

import java.util.List;
import java.util.Set;

public interface IncidentService {
	Set<Incident> getIncidents();

    boolean deleteIncident(Long id);

    boolean saveIncident(Incident incident);

	Incident getIncident(Long id);

	IncidentGroup getGroup(Long id);

	Set<IncidentGroup> getGroups();
	
	Set<IncidentChronology> getChronologies(Long id);
	
	Set<Product> getProducts(Long id);
	
	void notificationCheck();
	
	void dailyReport();
	
	void weekEndReport();
	
	void weeklyReport();

	boolean generateWeeklyReport(EmailAddress address);

	boolean generateIncidentReportByProduct(IncidentReportByProduct report);

	List<Incident> getOpenIncidents();

	boolean toggleAutoWeeklyReport(ToggleSwitch action);

	boolean isToggleAutoWeeklyReport();

	ErrorCondition getErrorCode(Long id);

	ReferenceData getApplicationStatus(Long id);
}
