package us.com.plattrk.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.Project;
import us.com.plattrk.repository.ProjectRepository;
import us.com.plattrk.api.model.PageWrapper;

@Service(value = "ProjectService")
public class ProjectServiceImpl implements ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<Project> getProjects() {
        return projectRepository.getProjects();
    }

    @Override
    @Transactional
    public PageWrapper<Project> search(Map<String, String> filtersMap) {
        return projectRepository.getProjectsByCriteria(filtersMap);
    }

    @Override
    @Transactional
    public Project deleteProject(Long id) {
        return projectRepository.deleteProject(id);
    }

    @Override
    @Transactional
    public Project saveProject(Project project) {
        return projectRepository.saveProject(project);
    }

    @Override
    public Optional<Project> getProject(Long id) {
        return projectRepository.getProject(id);
    }

}
