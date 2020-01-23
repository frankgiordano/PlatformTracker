package us.com.plattrk.service;

import java.util.Set;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;

public interface IncidentGroupService {
	
	Set<IncidentGroup> getGroups();
	
	IncidentGroup getGroup(Long id);

	Set<Incident> getGroupIncidents(Long id);

	boolean deleteGroup(Long id);

	boolean saveGroup(IncidentGroup group);

	boolean deleteAllGroupOrphans();

}
