package us.com.plattrk.service;
import java.util.Set;
import us.com.plattrk.api.model.Project;

public interface ProjectService {
	Set<Project> getProjects();
	
	boolean deleteProject(Long id);
	
	boolean saveProject(Project project);
	
	Project getProject(Long id);
}