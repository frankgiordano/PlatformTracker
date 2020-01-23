package us.com.plattrk.repository;

import java.util.Set;
import us.com.plattrk.api.model.Project;


public interface ProjectRepository {
	
    Set<Project> getProjects();

    boolean deleteProject(Long id);

    boolean saveProject(Project project);

	Project getProject(Long id);

}
