package us.com.plattrk.repository;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.api.model.PageWrapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IncidentGroupRepository {

    public Set<IncidentGroup> getGroups();

    public Set<Incident> getGroupIncidents(Long id);

    public IncidentGroup getGroup(Long id);

    public PageWrapper<IncidentGroup> getIncidentGroupsByCriteria(Map<String, String> filtersMap);

    public IncidentGroup deleteGroup(Long id);

    public IncidentGroup saveGroup(IncidentGroup group);

    public List<IncidentGroup> deleteAllOrphanGroups();
    
}
