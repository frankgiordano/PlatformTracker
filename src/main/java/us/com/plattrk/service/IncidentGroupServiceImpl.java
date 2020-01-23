package us.com.plattrk.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.repository.IncidentGroupRepository;

@Service(value = "IncidentGroupService")
public class IncidentGroupServiceImpl implements IncidentGroupService {
	
	@Autowired
	private IncidentGroupRepository incidentGroupRepository;

	public Set<IncidentGroup> getGroups() {
		return incidentGroupRepository.getGroups();
	}

	@Override
	public Set<Incident> getGroupIncidents(Long id) {
		return incidentGroupRepository.getGroupIncidents(id);
	}
	
	
	public IncidentGroup getGroup(Long id) {
		return incidentGroupRepository.getGroup(id);
	}

	@Override
	@Transactional
	public boolean deleteGroup(Long id) {
		return incidentGroupRepository.deleteGroup(id);
	}

	@Override
	@Transactional
	public boolean saveGroup(IncidentGroup group) {
		return incidentGroupRepository.saveGroup(group);
	}

	@Override
	@Transactional
	public boolean deleteAllGroupOrphans() {
		return incidentGroupRepository.deleteAllGroupOrphans();
	}

}
