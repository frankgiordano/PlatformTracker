package us.com.plattrk.repository;

import java.util.List;

import us.com.plattrk.api.model.Project;

public interface ProjectRepository {
	
    public List<Project> getProjects();

    public Project deleteProject(Long id);

    public Project saveProject(Project project);

	public Project getProject(Long id);

}
