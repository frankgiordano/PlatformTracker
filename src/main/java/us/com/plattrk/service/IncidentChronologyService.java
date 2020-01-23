package us.com.plattrk.service;

import java.util.Set;

import us.com.plattrk.api.model.IncidentChronology;

public interface IncidentChronologyService {

	boolean deleteIncidentChronology(Long id);

	boolean saveIncidentChronology(IncidentChronology chronology);

	Set<IncidentChronology> getChronologies();

	IncidentChronology getIncidentChronology(Long id);
	
}
