package us.com.plattrk.service;

import java.util.Set;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;

public interface IncidentGroupService {

	public Set<IncidentGroup> getGroups();

	public IncidentGroup getGroup(Long id);

	public Set<Incident> getGroupIncidents(Long id);

	public boolean deleteGroup(Long id);

	public boolean saveGroup(IncidentGroup group);

	public boolean deleteAllGroupOrphans();

}
