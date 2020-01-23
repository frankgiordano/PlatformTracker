package us.com.plattrk.repository;

import java.util.Set;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;

public interface IncidentChronologyRepository {

	boolean saveIncidentChronology(IncidentChronology chronology);

	boolean deleteIncidentChronology(Long id);

	Set<IncidentChronology> getChronologies();

	Set<IncidentChronology> getChronologiesPerIncident(Long id);

	Incident getIncidentOfChronology(Long id);

}
