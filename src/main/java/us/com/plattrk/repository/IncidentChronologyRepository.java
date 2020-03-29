package us.com.plattrk.repository;

import java.util.Set;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;

public interface IncidentChronologyRepository {

    public IncidentChronology saveIncidentChronology(IncidentChronology chronology);

    public IncidentChronology deleteIncidentChronology(Long id);

    public Set<IncidentChronology> getChronologies();

    public Set<IncidentChronology> getChronologiesPerIncident(Long id);

    public Incident getIncidentOfChronology(Long id);

}
