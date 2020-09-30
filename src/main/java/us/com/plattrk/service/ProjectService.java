package us.com.plattrk.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import us.com.plattrk.api.model.Project;
import us.com.plattrk.util.PageWrapper;

public interface ProjectService {

    public List<Project> getProjects();

    public PageWrapper<Project> search(Map<String, String> filtersMap);
    
    public Project deleteProject(Long id);
    
    public Project saveProject(Project project);
    
    public Optional<Project> getProject(Long id);
    
}