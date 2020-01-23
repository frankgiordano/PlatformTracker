package us.com.plattrk.repository;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.Project;

@Repository
public class ProjectRepositoryImpl implements
		ProjectRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Set<Project> getProjects() {
		@SuppressWarnings("unchecked")
		List<Project> myResult = em.createNamedQuery(
				Project.FIND_ALL_PROJECTS).getResultList();
		Set<Project> projects = new HashSet<Project>(
				myResult);
		return projects;
	}

	@Override
	public boolean deleteProject(Long id) {
		try {
			Project project = em.find(
					Project.class, id);
			Set <IncidentResolution> resolutions = project.getResolutions();
			for (IncidentResolution item : resolutions){
				item.setResolutionProject(null);	
			}
			em.remove(project);
			em.flush();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Transactional
	@Override
	public boolean saveProject(Project project) {
		try {
			if (project.getId() == null) {
				em.persist(project);
				em.flush();
			} else {
				em.merge(project);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;

	}

	@Override
	public Project getProject(Long id) {
		Project incidentProject = em.find(
				Project.class, id);
		return incidentProject;

	}

}
