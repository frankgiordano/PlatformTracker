package us.com.plattrk.repository;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.Project;

@Repository
public class ProjectRepositoryImpl implements ProjectRepository {

    private static Logger log = LoggerFactory.getLogger(ProjectRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Project> getProjects() {
        List<Project> myResult = em.createNamedQuery(Project.FIND_ALL_PROJECTS).getResultList();
        return myResult;
    }

    @Override
    public Project deleteProject(Long id) {
        Project project = null;
        try {
            project = em.find(Project.class, id);
            Set<IncidentResolution> resolutions = project.getResolutions();
            for (IncidentResolution item : resolutions) {
                item.setResolutionProject(null);
            }
            em.remove(project);
            em.flush();
        } catch (PersistenceException e) {
            log.error("ProjectRepositoryImpl::deleteProject - failure deleting project id " + id + ", msg = " + e.getMessage());
            throw (e);
        }

        return project;
    }

    @Override
    public Project saveProject(Project project) {
        try {
            if (project.getId() == null) {
                em.persist(project);
                em.flush();
            } else {
                em.merge(project);
            }
        } catch (PersistenceException e) {
            log.error("ProductRepositoryImpl::saveProject - failure saving product = " + project.toString() + ", msg = " + e.getMessage());
            throw (e);
        }

        return project;
    }

    @Override
    public Project getProject(Long id) {
        Project incidentProject = em.find(Project.class, id);
        return incidentProject;
    }

}
