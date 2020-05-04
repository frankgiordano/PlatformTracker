package us.com.plattrk.repository;

import java.util.List;

import us.com.plattrk.api.model.Project;
import us.com.plattrk.util.PageWrapper;

public interface ProjectRepository {
    
    public List<Project> getProjects();

    public PageWrapper<Project> getProjectsByCriteria(String searchTerm, Long pageIndex);

    public Project deleteProject(Long id);

    public Project saveProject(Project project);

    public Project getProject(Long id);

}
