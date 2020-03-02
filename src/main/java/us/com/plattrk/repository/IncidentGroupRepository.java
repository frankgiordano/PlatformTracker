package us.com.plattrk.repository;

import java.util.Set;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;

public interface IncidentGroupRepository {

	public Set<IncidentGroup> getGroups();

	public Set<Incident> getGroupIncidents(Long id);

	public IncidentGroup getGroup(Long id);

	public boolean deleteGroup(Long id);

	public IncidentGroup saveGroup(IncidentGroup group);

	public boolean deleteAllGroupOrphans();
	
}
