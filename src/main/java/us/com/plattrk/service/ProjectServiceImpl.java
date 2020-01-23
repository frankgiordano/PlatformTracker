package us.com.plattrk.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.Project;
import us.com.plattrk.repository.ProjectRepository;
import us.com.plattrk.repository.ReferenceDataRepository;

@Service(value = "ProjectService")
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private IncidentGroupService incidentGroupService;
	
	@Autowired
	private ReferenceDataRepository refRepository;

	@Override
	public Set<Project> getProjects() {
		return projectRepository.getProjects();
	}
	
	
	@Override
	public Project getProject(Long id) {
		return projectRepository.getProject(id);
	}
	
	
	@Override
	@Transactional
	public boolean deleteProject(Long id) {
		return projectRepository.deleteProject(id);
	}

	@Override
	@Transactional
	public boolean saveProject(Project project) {
/*		Long incidentGroupId = project.getIncidentGroupId();
		if(incidentGroupId != null){
			IncidentGroup incidentGroup = incidentGroupService.getGroup(incidentGroupId);
			if(incidentGroup != null)
				project.setIncidentGroup(incidentGroup);
			else
				throw new RuntimeException("no incident existed with id "+incidentGroupId);
		}
		Long statusId = project.getStatusId();
		if(  statusId  != null){
			 ReferenceData status =  refRepository.getReferenceData(statusId);
			 if(status != null)
				project.setStatus(status);
			else
				throw new RuntimeException("no incident existed with id "+statusId);
		}
		Long categoryId = project.getCategoryId();
		if(  categoryId  != null){
			 ReferenceData category =  refRepository.getReferenceData(categoryId);
			 if(category != null)
				project.setCategory(category);
			else
				throw new RuntimeException("no incident existed with id "+categoryId);
		}
		
		
		Long resourceId = project.getResourceId();
		if(  resourceId  != null){
			 ReferenceData resource =  refRepository.getReferenceData(resourceId);
			 if(resource != null)
				project.setResource(resource);
			else
				throw new RuntimeException("no incident existed with id "+resourceId);
		}*/
		
		
		return projectRepository.saveProject(project);
	}
	
	
	


}
