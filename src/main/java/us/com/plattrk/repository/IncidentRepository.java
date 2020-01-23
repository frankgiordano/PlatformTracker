package us.com.plattrk.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import us.com.plattrk.api.model.ErrorCondition;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.api.model.ReferenceData;

public interface IncidentRepository {
	
    Set<Incident> getIncidents();

    boolean deleteIncident(Long id);

    boolean saveIncident(Incident incident);

	Incident getIncident(Long id);

	IncidentGroup getGroup(Long id);

	Set<IncidentGroup> getGroups();

	Set<IncidentChronology> getChronologies(Long id);

	Set<Product> getProducts(Long id);

	List<Incident> getOpenIncidents();

	boolean isIncidentOpen(Long id);

	List<Incident> getDateRangeIncidents(Date start, Date end);

	List<Incident> getDateRangeIncidentsByPriority(Date start, Date end, String priority);

	List<Incident> getDateRangeIncidentsByApplicationStatus(Date start, Date end, String applicationStatus);

	ErrorCondition getErrorCode(Long id);

	ReferenceData getApplicationStatus(Long id);

}
