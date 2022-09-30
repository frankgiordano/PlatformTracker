package us.com.plattrk.repository;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.api.model.PageWrapper;
import us.com.plattrk.util.RepositoryUtil;

@Repository
public class IncidentGroupRepositoryImpl implements IncidentGroupRepository {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentGroupRepositoryImpl.class);

    @Autowired
    private RepositoryUtil<IncidentGroup> repositoryUtil;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Set<IncidentGroup> getGroups() {
        @SuppressWarnings("unchecked")
        List<IncidentGroup> myResult = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS).getResultList();
        Set<IncidentGroup> groups = new HashSet<IncidentGroup>(myResult);
        return groups;
    }

    @Override
    public Set<Incident> getGroupIncidents(Long id) {
        IncidentGroup incidentGroup = em.find(IncidentGroup.class, id);
        Optional<Set<Incident>> incidents = Optional.of(incidentGroup.getIncidents());
        return incidents.orElse(new HashSet<>());
    }

    @Override
    public IncidentGroup getGroup(Long id) {
        IncidentGroup incidentGroup = em.find(IncidentGroup.class, id);
        incidentGroup.setIncidents(null);
        return incidentGroup;
    }

    @Override
    public PageWrapper<IncidentGroup> getIncidentGroupsByCriteria(Map<String, String> filtersMap) {
        String name = filtersMap.get("name");
        String desc = filtersMap.get("desc");
        Long pageIndex = Long.parseLong(filtersMap.get("pageIndex"));

        boolean isGrpNameEmpty = "*".equals(name);
        boolean isDescEmpty = "*".equals(desc);
        name = repositoryUtil.appendWildCard(name);
        desc = repositoryUtil.appendWildCard(desc);

        Query query;
        List<IncidentGroup> result;
        Long total;
        if (isGrpNameEmpty && isDescEmpty) {
            query = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS);
            result = repositoryUtil.criteriaResults(pageIndex, query);
            query = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS_COUNT);
            total = (long) query.getSingleResult();
        } else if (!isGrpNameEmpty && isDescEmpty) {
            query = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS_BY_NAME_CRITERIA)
                      .setParameter("name", name);
            result = repositoryUtil.criteriaResults(pageIndex, query);
            query = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS_COUNT_BY_NAME_CRITERIA)
                      .setParameter("name", name);
            total = (long) query.getSingleResult();
        } else if (isGrpNameEmpty) {
            query = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS_BY_DESC_CRITERIA)
                      .setParameter("desc", desc);
            result = repositoryUtil.criteriaResults(pageIndex, query);
            query = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS_COUNT_BY_DESC_CRITERIA)
                      .setParameter("desc", desc);
            total = (long) query.getSingleResult();
        } else {
            query = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS_BY_BOTH_CRITERIA)
                      .setParameter("name", name)
                      .setParameter("desc", desc);
            result = repositoryUtil.criteriaResults(pageIndex, query);
            query = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS_COUNT_BY_BOTH_CRITERIA)
                      .setParameter("name", name)
                      .setParameter("desc", desc);
            total = (long) query.getSingleResult();
        }

        return new PageWrapper<IncidentGroup>(result, total);
    }

    @Override
    public IncidentGroup deleteGroup(Long id) {
        Optional<IncidentGroup> group = Optional.of(em.find(IncidentGroup.class, id));
        group.ifPresent(lambdaWrapper(g -> {
            em.remove(g);
            em.flush();
        }));

        Optional<IncidentGroup> groupCheck = Optional.of(em.find(IncidentGroup.class, id));
        if (groupCheck.isPresent()) {
            throw new PersistenceException("ConstraintErrorException");
        }

        return group.orElse(null);
    }

    @Override
    public IncidentGroup saveGroup(IncidentGroup group) {
        try {
            if (group.getId() == null) {
                em.persist(group);
                em.flush();
            } else {
                em.merge(group);
            }
        } catch (PersistenceException e) {
            LOG.error("IncidentGroupRepositoryImpl::saveGroup - failure saving group {}, msg {}", group, e.getMessage());
            throw (e);
        }

        return group;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<IncidentGroup> deleteAllOrphanGroups() {
        Predicate<IncidentGroup> isEmptyResolutions = group -> isEmptyGroupResolutions(group);
        Predicate<IncidentGroup> isEmptyRootCauses = group -> isEmptyGroupRootCause(group);
        Predicate<IncidentGroup> isEmptyIncidents = group -> group.getIncidents().isEmpty();

        List<IncidentGroup> myResult = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS_RELATIONS).getResultList();
        List<IncidentGroup> removeList = myResult.stream().filter(isEmptyIncidents.and(isEmptyResolutions).and(isEmptyRootCauses))
                                                 .collect(Collectors.toList());

        removeList.forEach(lambdaWrapper(item -> {
            em.remove(item);
            em.flush();
        }, removeList));

        return removeList;
    }

    @SuppressWarnings("rawtypes")
    private Boolean isEmptyGroupResolutions(IncidentGroup group) {
        List resolutionChildIds = em.createQuery("select c.id from IncidentResolution c where c.incidentGroup.id = :pid").setParameter("pid", group.getId()).getResultList();
        return resolutionChildIds.isEmpty();
    }

    @SuppressWarnings("rawtypes")
    private Boolean isEmptyGroupRootCause(IncidentGroup group) {
        List rcaChildIds = em.createQuery("select c.id from RCA c where c.incidentGroup.id = :pid").setParameter("pid", group.getId()).getResultList();
        return rcaChildIds.isEmpty();
    }

    private static Consumer<IncidentGroup> lambdaWrapper(Consumer<IncidentGroup> consumer, List<IncidentGroup> groups) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                LOG.error("IncidentGroupRepositoryImpl::deleteAllOrphanGroups - failure deleting orphan group {}, msg {}", i.toString(), e.getMessage());
                groups.remove(i);
            }
        };
    }

    private static Consumer<IncidentGroup> lambdaWrapper(Consumer<IncidentGroup> consumer) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                LOG.error("IncidentGroupRepositoryImpl::deleteGroup - failure deleting group id {}, msg {}", i.getId(), e.getMessage());
                throw (e);
            }
        };
    }

}
