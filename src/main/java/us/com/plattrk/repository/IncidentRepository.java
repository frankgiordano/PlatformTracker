package us.com.plattrk.repository;

import java.util.*;

import us.com.plattrk.api.model.*;
import us.com.plattrk.api.model.PageWrapper;

public interface IncidentRepository {

    public Set<Incident> getIncidents();

    public PageWrapper<Incident> getIncidentsByCriteria(Map<String, String> filtersMap);

    public Incident deleteIncident(Long id);

    public Incident saveIncident(Incident incident);

    public boolean isIncidentOpen(Long id);

    public List<Incident> getDateRangeIncidents(Date start, Date end);

    public List<Incident> getDateRangeIncidentsByPriority(Date start, Date end, String priority);

    public List<Incident> getDateRangeIncidentsByApplicationStatus(Date start, Date end, String applicationStatus);

    public IncidentGroup getGroup(Long id);

    public Set<IncidentGroup> getGroups();

    public Set<IncidentChronology> getChronologies(Long id);

    public Set<Product> getProducts(Long id);

    public List<Incident> getOpenIncidents();

    public ErrorCondition getErrorCode(Long id);

    public ReferenceData getApplicationStatus(Long id);

    public Optional<Incident> getIncident(Long id);

}
