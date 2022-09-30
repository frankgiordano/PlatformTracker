package us.com.plattrk.service;

import us.com.plattrk.api.model.IncidentChronology;

import java.util.Set;

public interface IncidentChronologyService {

    public IncidentChronology deleteIncidentChronology(Long id);

    public IncidentChronology saveIncidentChronology(IncidentChronology chronology) throws Exception;

    public Set<IncidentChronology> getChronologies();

    public IncidentChronology getIncidentChronology(Long id);
    
}
