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
import us.com.plattrk.util.PageWrapper;

public interface IncidentRepository {

    public Set<Incident> getIncidents();

    public PageWrapper<Incident> getIncidentsByCriteria(String searchTerm, Long pageIndex);

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

    public Incident getIncident(Long id);

}
