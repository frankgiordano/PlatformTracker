package us.com.plattrk.service;

import java.util.List;
import java.util.Set;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.util.PageWrapper;

public interface IncidentGroupService {

    public Set<IncidentGroup> getGroups();

    public IncidentGroup getGroup(Long id);

    public Set<Incident> getGroupIncidents(Long id);

    public PageWrapper<IncidentGroup> search(String searchTerm, Long pageIndex);

    public IncidentGroup deleteGroup(Long id);

    public IncidentGroup saveGroup(IncidentGroup group);

    public List<IncidentGroup> deleteAllOrphanGroups();

}
