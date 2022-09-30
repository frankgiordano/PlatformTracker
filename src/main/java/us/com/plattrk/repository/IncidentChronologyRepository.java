package us.com.plattrk.repository;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;

import java.util.Set;

public interface IncidentChronologyRepository {

    public IncidentChronology saveIncidentChronology(IncidentChronology chronology);

    public IncidentChronology deleteIncidentChronology(Long id);

    public Set<IncidentChronology> getChronologies();

    public Set<IncidentChronology> getChronologiesPerIncident(Long id);

    public Incident getIncidentOfNewChronology(Long id);

    public IncidentChronology getIncidentChronology(Long id);

}
