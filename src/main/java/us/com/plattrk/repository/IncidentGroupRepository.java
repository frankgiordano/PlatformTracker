package us.com.plattrk.repository;

import java.util.Set;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;

public interface IncidentGroupRepository {

	Set<IncidentGroup> getGroups();

	Set<Incident> getGroupIncidents(Long id);

	IncidentGroup getGroup(Long id);

	boolean deleteGroup(Long id);

	boolean saveGroup(IncidentGroup group);

	boolean deleteAllGroupOrphans();
	
}
