package us.com.plattrk.service;
import java.util.List;

import us.com.plattrk.api.model.Project;

public interface ProjectService {

	List<Project> getProjects();
	
	boolean deleteProject(Long id);
	
	boolean saveProject(Project project);
	
	Project getProject(Long id);
	
}