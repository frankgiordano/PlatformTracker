package us.com.plattrk.service;

import java.util.Set;

import us.com.plattrk.api.model.IncidentChronology;

public interface IncidentChronologyService {

    public IncidentChronology deleteIncidentChronology(Long id);

    public IncidentChronology saveIncidentChronology(IncidentChronology chronology);

    public Set<IncidentChronology> getChronologies();

    public IncidentChronology getIncidentChronology(Long id);
    
}
