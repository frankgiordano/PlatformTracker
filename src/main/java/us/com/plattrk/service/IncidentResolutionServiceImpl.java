package us.com.plattrk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.IncidentResolutionVO;
import us.com.plattrk.api.model.Project;
import us.com.plattrk.api.model.Status;
import us.com.plattrk.repository.IncidentResolutionRepository;
import us.com.plattrk.repository.ProjectRepository;
import us.com.plattrk.util.PageWrapper;

@Service(value = "IncidentResolutionService")
public class IncidentResolutionServiceImpl implements IncidentResolutionService {

    private static Logger log = LoggerFactory.getLogger(IncidentResolutionServiceImpl.class);

    @Autowired
    private IncidentResolutionRepository resolutionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<IncidentResolution> getIncidentResolutions() {
        return resolutionRepository.getResolutions();
    }

    @Override
    @Transactional
    public PageWrapper<IncidentResolution> search(String searchTerm, Long pageIndex) {
        return resolutionRepository.getResolutionsByCriteria(searchTerm, pageIndex);
    }

    @Override
    @Transactional
    public IncidentResolution deleteResolution(Long id) {
        return resolutionRepository.deleteResolution(id);
    }

    @Override
    @Transactional
    public IncidentResolution saveResolution(IncidentResolution incidentResolution) {
        return resolutionRepository.saveResolution(incidentResolution);
    }

    @Transactional
    public List<IncidentResolutionVO> saveLinkedResolutions(List<IncidentResolutionVO> resolutions) {
        List<IncidentResolution> updates = new ArrayList<IncidentResolution>();
        Predicate<IncidentResolutionVO> isProjectId = resolution -> resolution.getProjectId() != null;
        Predicate<IncidentResolutionVO> isProjectIdPositive = resolution -> resolution.getProjectId() >= 0;

        List<IncidentResolutionVO> saveResList = resolutions.stream().filter(isProjectId.and(isProjectIdPositive)).collect(Collectors.toList());
        saveResList.forEach(resVO -> {
            IncidentResolution resolution = resolutionRepository.getResolution(resVO.getId());
            Project project = projectRepository.getProject(resVO.getProjectId());
            determineUpdate(updates, resVO, resolution, project);
        });

        List<IncidentResolution> result = resolutionRepository.saveResolutions(updates);
        updateIncomingResolutions(resolutions, result);

        // send the updated incoming resolutions which contains the resolutions that were updated for the project
        return resolutions;
    }

    @Override
    public List<Status> getStatusList() {
        return resolutionRepository.getStatusList();
    }

    @Override
    public List<IncidentResolution> getGroupResolutions(Long id) {
        return resolutionRepository.getGroupResolutions(id);
    }

    @Override
    public IncidentResolution getIncidentResolution(Long id) {
        return resolutionRepository.getResolution(id);
    }

    private void updateIncomingResolutions(List<IncidentResolutionVO> resolutions, List<IncidentResolution> result) {
        List<Long> ids = new ArrayList<Long>();
        result.forEach(item -> {
            ids.add(item.getId());
        });
        resolutions.forEach(item -> {
            if (!ids.contains(item.getId()))
                resolutions.remove(item);
        });
    }

    private void determineUpdate(List<IncidentResolution> updates, IncidentResolutionVO resVO, IncidentResolution resolution, Project project) {
        int operation = resVO.getOperation();
        switch (operation) {
            case IncidentResolutionVO.Operation.ATTACH:
                resolution.setResolutionProject(project);
                updates.add(resolution);
                break;
            case IncidentResolutionVO.Operation.REMOVE:
                resolution.setResolutionProject(null);
                updates.add(resolution);
                break;
            default:
                log.error("IncidentResolutionServiceImpl::determineUpdate - ignoring no operation value given.");
        }
    }

}
