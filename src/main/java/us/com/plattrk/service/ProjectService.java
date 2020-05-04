package us.com.plattrk.service;

import java.util.List;

import us.com.plattrk.api.model.Project;
import us.com.plattrk.util.PageWrapper;

public interface ProjectService {

    public List<Project> getProjects();

    public PageWrapper<Project> search(String searchTerm, Long pageIndex);
    
    public Project deleteProject(Long id);
    
    public Project saveProject(Project project);
    
    public Project getProject(Long id);
    
}