package us.com.plattrk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.IncidentResolutionVO;
import us.com.plattrk.api.model.Project;
import us.com.plattrk.api.model.Status;
import us.com.plattrk.repository.IncidentResolutionRepository;
import us.com.plattrk.repository.ProjectRepository;

@Service(value = "IncidentResolutionService")
public class IncidentResolutionServiceImpl implements IncidentResolutionService {

    @Autowired
    private IncidentResolutionRepository resolutionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    @Transactional
    public IncidentResolution deleteResolution(Long id) {
        return resolutionRepository.deleteResolution(id);
    }

    @Override
    public IncidentResolution getIncidentResolution(Long id) {
        return resolutionRepository.getResolution(id);
    }

    @Override
    public List<IncidentResolution> getIncidentResolutions() {
        return resolutionRepository.getResolutions();
    }

    @Override
    public List<Status> getStatusList() {
        return resolutionRepository.getStatusList();
    }

    @Transactional
    public List<IncidentResolution> saveLinkedResolutions(List<IncidentResolutionVO> resolutions) {
        List<IncidentResolution> inserts = new ArrayList<IncidentResolution>();
        Predicate<IncidentResolutionVO> isProjectId = resolution -> resolution.getProjectId() != null;
        Predicate<IncidentResolutionVO> isProjectIdPositive = resolution -> resolution.getProjectId() >= 0;

        List<IncidentResolutionVO> saveResList = resolutions.stream().filter(isProjectId.and(isProjectIdPositive)).collect(Collectors.toList());
        saveResList.forEach(resVO -> {
            IncidentResolution resolution = resolutionRepository.getResolution(resVO.getId());
            Project project = projectRepository.getProject(resVO.getProjectId());
            if (resVO.getOperation() == IncidentResolutionVO.Operation.INSERT)
                resolution.setResolutionProject(project);
            else
                resolution.setResolutionProject(null);
            inserts.add(resolution);
        });

        return resolutionRepository.saveResolutions(inserts);
    }

    @Override
    @Transactional
    public IncidentResolution saveResolution(IncidentResolution incidentResolution) {
        return resolutionRepository.saveResolution(incidentResolution);
    }

    @Override
    public List<IncidentResolution> getGroupResolutions(Long id) {
        return resolutionRepository.getGroupResolutions(id);
    }

}
