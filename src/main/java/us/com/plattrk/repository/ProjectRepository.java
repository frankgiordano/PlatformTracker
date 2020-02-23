package us.com.plattrk.repository;

import java.util.List;

import us.com.plattrk.api.model.Project;

public interface ProjectRepository {
	
    List<Project> getProjects();

    boolean deleteProject(Long id);

    boolean saveProject(Project project);

	Project getProject(Long id);

}
