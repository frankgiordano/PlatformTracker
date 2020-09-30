package us.com.plattrk.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import us.com.plattrk.api.model.Project;
import us.com.plattrk.api.model.PageWrapper;

public interface ProjectRepository {
    
    public List<Project> getProjects();

    public PageWrapper<Project> getProjectsByCriteria(Map<String, String> filtersMap);

    public Project deleteProject(Long id);

    public Project saveProject(Project project);

    public Optional<Project> getProject(Long id);

}
