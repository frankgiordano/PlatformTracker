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
		return projectRepository.saveProject(project);
	}

}
