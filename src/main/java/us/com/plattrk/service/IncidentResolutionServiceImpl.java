package us.com.plattrk.service;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private IncidentGroupService incidentGroupService;

    @Override
    @Transactional
    public boolean deleteResolution(Long id) {
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

    public boolean saveLinkedResolutions(List<IncidentResolutionVO> resolutions) {
        List<IncidentResolution> inserts = new ArrayList<IncidentResolution>();
        for (IncidentResolutionVO item : resolutions) {
            Long rid = item.getId();
            Long pid = item.getProjectId();
            int operation = item.getOperation();
            if (pid == null || pid < 0) {
                continue;
            }
            IncidentResolution resolution = resolutionRepository.getResolution(rid);
            Project project = projectRepository.getProject(pid);
            if (operation == IncidentResolutionVO.Operation.INSERT)
                resolution.setResolutionProject(project);
            else
                resolution.setResolutionProject(null);
            inserts.add(resolution);
        }
        return resolutionRepository.saveResolutions(inserts);
    }

    @Override
    @Transactional
    public boolean saveResolution(IncidentResolution incidentResolution) {
        return resolutionRepository.saveResolution(incidentResolution);
    }

    @Override
    public List<IncidentResolution> getGroupResolutions(Long id) {
        return resolutionRepository.getGroupResolutions(id);
    }

}
