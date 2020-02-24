package us.com.plattrk.service;

import java.util.Set;

import us.com.plattrk.api.model.IncidentChronology;

public interface IncidentChronologyService {

	public boolean deleteIncidentChronology(Long id);

	public boolean saveIncidentChronology(IncidentChronology chronology);

	public Set<IncidentChronology> getChronologies();

	public IncidentChronology getIncidentChronology(Long id);
	
}
