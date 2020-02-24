package us.com.plattrk.service;
import java.util.List;

import us.com.plattrk.api.model.Project;

public interface ProjectService {

	public List<Project> getProjects();
	
	public boolean deleteProject(Long id);
	
	public boolean saveProject(Project project);
	
	public Project getProject(Long id);
	
}