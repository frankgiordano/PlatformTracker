package us.com.plattrk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.api.model.PageWrapper;
import us.com.plattrk.repository.IncidentGroupRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(value = "IncidentGroupService")
public class IncidentGroupServiceImpl implements IncidentGroupService {
    
    @Autowired
    private IncidentGroupRepository incidentGroupRepository;

    @Override
    public Set<IncidentGroup> getGroups() {
        return incidentGroupRepository.getGroups();
    }

    @Override
    public Set<Incident> getGroupIncidents(Long id) {
        return incidentGroupRepository.getGroupIncidents(id);
    }

    @Override
    public IncidentGroup getGroup(Long id) {
        return incidentGroupRepository.getGroup(id);
    }

    @Override
    @Transactional
    public PageWrapper<IncidentGroup> search(Map<String, String> filtersMap) {
        return incidentGroupRepository.getIncidentGroupsByCriteria(filtersMap);
    }

    @Override
    @Transactional
    public IncidentGroup deleteGroup(Long id) {
        return incidentGroupRepository.deleteGroup(id);
    }

    @Override
    @Transactional
    public IncidentGroup saveGroup(IncidentGroup group) {
        return incidentGroupRepository.saveGroup(group);
    }

    @Override
    @Transactional
    public List<IncidentGroup> deleteAllOrphanGroups() {
        return incidentGroupRepository.deleteAllOrphanGroups();
    }

}
