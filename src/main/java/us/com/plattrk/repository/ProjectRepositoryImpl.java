package us.com.plattrk.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

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
        Optional<Project> project = Optional.of(em.find(Project.class, id));
        project.ifPresent(lambdaWrapper(p -> {
            Set<IncidentResolution> resolutions = p.getResolutions();
            // get project's associated resolutions and in each resolution null out its link to this project
            resolutions.forEach((element -> element.setResolutionProject(null)));
            em.remove(p);
            em.flush();
        }));

        return project.orElse(null);
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

    private static Consumer<Project> lambdaWrapper(Consumer<Project> consumer) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                log.error("ProjectRepositoryImpl::deleteProject - failure deleting project id " + i.getId() + ", msg = " + e.getMessage());
                throw (e);
            }
        };
    }

}
