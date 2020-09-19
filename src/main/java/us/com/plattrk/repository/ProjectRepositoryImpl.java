package us.com.plattrk.repository;

import java.util.*;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.Project;
import us.com.plattrk.util.PageWrapper;
import us.com.plattrk.util.QueryResult;
import us.com.plattrk.util.RepositoryUtil;

@Repository
public class ProjectRepositoryImpl implements ProjectRepository {

    private static final Logger log = LoggerFactory.getLogger(ProjectRepositoryImpl.class);
    private static final String TYPE = "Project";

    @Autowired
    private RepositoryUtil<Project> repositoryUtil;

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<Project> getProjects() {
        List<Project> myResult = em.createNamedQuery(Project.FIND_ALL_PROJECTS).getResultList();
        return myResult;
    }

    @Override
    public PageWrapper<Project> getProjectsByCriteria(Map<String, String> filtersMap) {
        String name = filtersMap.get("name");
        String owner = filtersMap.get("assignee");
        Long pageIndex = Long.parseLong(filtersMap.get("pageIndex"));

        boolean isNameEmpty = "*".equals(name);
        boolean isOwnerEmpty = "*".equals(owner) || "undefined".equals(owner);
        name = repositoryUtil.appendWildCard(name);

        QueryResult<Project> queryResult;
        Map<String, String> columnInfo = new HashMap<String, String>();

        if (isNameEmpty) {
            String queryName = Project.FIND_ALL_PROJECTS;
            String queryCountName = Project.FIND_ALL_PROJECTS_COUNT;
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else {
            String queryName = Project.FIND_ALL_PROJECTS_BY_CRITERIA;
            String queryCountName = Project.FIND_ALL_PROJECTS_COUNT_BY_CRITERIA;
            columnInfo.put("name", name);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        }

        return new PageWrapper<Project>(queryResult.result, queryResult.total);
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
            log.error("ProjectRepositoryImpl::saveProject - failure saving product = " + project.toString() + ", msg = " + e.getMessage());
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
