package us.com.plattrk.repository;

import java.util.*;
import java.util.function.Consumer;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.*;
import us.com.plattrk.api.model.PageWrapper;
import us.com.plattrk.api.model.QueryResult;
import us.com.plattrk.util.RepositoryUtil;

@Repository
public class IncidentRepositoryImpl implements IncidentRepository {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentRepositoryImpl.class);
    private static final String TYPE = "Incident";

    @Autowired
    private RepositoryUtil<Incident> repositoryUtil;

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public Set<Incident> getIncidents() {
        List<Incident> myResult = em.createNamedQuery(Incident.FIND_ALL_INCIDENTS).getResultList();
        Set<Incident> incidents = new HashSet<Incident>(myResult);
        return incidents;
    }

    @Override
    public PageWrapper<Incident> getIncidentsByCriteria(Map<String, String> filtersMap) {
        String tag = filtersMap.get("tag");
        String desc = filtersMap.get("desc");
        String owner = filtersMap.get("assignee");
        Long pageIndex = Long.parseLong(filtersMap.get("pageIndex"));

        boolean isTagEmpty = "*".equals(tag);
        boolean isDescEmpty = "*".equals(desc);
        boolean isOwnerEmpty = "*".equals(owner) || "undefined".equals(owner);
        tag = repositoryUtil.appendWildCard(tag);
        desc = repositoryUtil.appendWildCard(desc);

        QueryResult<Incident> queryResult;
        Map<String, String> columnInfo = new HashMap<>();

        if (isTagEmpty && isDescEmpty) {
            String queryName = Incident.FIND_ALL_INCIDENTS;
            String queryCountName = Incident.FIND_ALL_INCIDENTS_COUNT;
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else if (!isTagEmpty && isDescEmpty) {
            String queryName = Incident.FIND_ALL_INCIDENTS_BY_TAG_CRITERIA;
            String queryCountName = Incident.FIND_ALL_INCIDENTS_COUNT_BY_TAG_CRITERIA;
            columnInfo.put("tag", tag);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else if (isTagEmpty) {
            String queryName = Incident.FIND_ALL_INCIDENTS_BY_DESC_CRITERIA;
            String queryCountName = Incident.FIND_ALL_INCIDENTS_COUNT_BY_DESC_CRITERIA;
            columnInfo.put("desc", desc);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else {
            String queryName = Incident.FIND_ALL_INCIDENTS_BY_BOTH_CRITERIA;
            String queryCountName = Incident.FIND_ALL_INCIDENTS_COUNT_BY_BOTH_CRITERIA;
            columnInfo.put("tag", tag);
            columnInfo.put("desc", desc);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        }

        return new PageWrapper<Incident>(queryResult.result, queryResult.total);
    }

    @Override
    public Incident deleteIncident(Long id) {
        Optional<Incident> incident = Optional.of(em.find(Incident.class, id));
        incident.ifPresent(lambdaWrapper(i -> {
            em.remove(i);
            em.flush();
        }));

        return incident.orElse(null);
    }

    @Override
    public Incident saveIncident(Incident incident) throws OptimisticLockException {
        Set<Product> products = new HashSet<>();
        ErrorCondition errorCode = new ErrorCondition();
        ReferenceData applicationStatus = new ReferenceData();

        try {
            if (incident.getId() == null) {
                // check to see if newly incoming incident has products attached.
                // The entity I am saving is new, but it has a relationship with
                // an existing entity (manytomany).  The solution is to use persist to save the
                // entity without the relationship, then add the relationship and call merge.
                products = incident.getProducts();
                if (!products.isEmpty()) {
                    incident.setProducts(null);
                }

                // this is a detach entity remove from incoming new incident and add it later for merge
                errorCode = incident.getError();
                incident.setError(null);

                // this is a detach entity remove from incoming new incident and add it later for merge
                applicationStatus = incident.getApplicationStatus();
                incident.setApplicationStatus(null);

                // check to see if newly incoming incident has a group attached and see if this group exists or does not.  If not,
                // allow the persist to create a new incident group.  If it does exist, attached the existing group id to this
                // incident for reuse.
                if (incident.getIncidentGroup() != null) {
                    List<IncidentGroup> incidentGroup = CheckIncidentGroup(incident.getIncidentGroup().getName());
                    if (!incidentGroup.isEmpty()) {
                        incident.setIncidentGroup(incidentGroup.get(0));
                    }
                }
                em.persist(incident);
                LOG.info("IncidentRepositoryImpl::saveIncident - incident id {} created",  incident.getId());
                em.flush();
            } else {
                // Due to the problem with bidirectional mapping between two tables, i.e.:incident and incident_group,
                // updating an existing incident from the UI is handled as a separate JSON request. As such,
                // for incoming update of an existing incident we need to check the group specified as done above and
                // proceed in a similar manner.
                if (incident.getIncidentGroup() != null) {
                    if (incident.getIncidentGroup().getId() == null) {
                        List<IncidentGroup> incidentGroup = CheckIncidentGroup(incident.getIncidentGroup().getName());
                        if (!incidentGroup.isEmpty()) {
                            incident.setIncidentGroup(incidentGroup.get(0));
                        }
                    }
                }
                em.merge(incident);
            }

            // check if there were incoming products attached during incident create
            if (!products.isEmpty()) {
                Incident incidentSaved = em.find(Incident.class, incident.getId());
                incidentSaved.setProducts(products);
                em.merge(incidentSaved);
            }

            // do the same for error condition
            if (incident.getError() == null) {
                Incident incidentSaved = em.find(Incident.class, incident.getId());
                ErrorCondition errorCodeSaved = em.find(ErrorCondition.class, errorCode.getId());
                incidentSaved.setError(errorCodeSaved);
                em.merge(incidentSaved);
            }

            // do the same for applicationStatus
            if (incident.getApplicationStatus() == null) {
                Incident incidentSaved = em.find(Incident.class, incident.getId());
                if (applicationStatus != null) {
                    ReferenceData applicationStatusSaved = em.find(ReferenceData.class, applicationStatus.getId());
                    incidentSaved.setApplicationStatus(applicationStatusSaved);
                    em.merge(incidentSaved);
                }
            }
        } catch (PersistenceException e) {
            LOG.error("IncidentRepositoryImpl::saveIncident - failure saving product {}, msg {}", incident.toString(), e.getMessage());
            throw (e);
        }

        return incident;
    }

    @Override
    public boolean isIncidentOpen(Long id) {
        Incident incident = em.find(Incident.class, id);
        if ("Open".equals(incident.getStatus())) {
            return true;
        }
        return false;
    }

    @Override
    public List<Incident> getDateRangeIncidents(Date start, Date end) {
        TypedQuery<Incident> query = em.createNamedQuery(Incident.FIND_ALL_OPEN_INCIDENTS_BY_RANGE_RELATIONS, Incident.class)
                                       .setParameter("startDate", start, TemporalType.DATE)
                                       .setParameter("endDate", end, TemporalType.DATE);
        return query.getResultList();
    }

    @Override
    public List<Incident> getDateRangeIncidentsByPriority(Date start, Date end, String priority) {
        TypedQuery<Incident> query = em.createNamedQuery(Incident.FIND_ALL_OPEN_INCIDENTS_BY_RANGE_AND_PRIORITY_RELATIONS, Incident.class)
                                       .setParameter("startDate", start, TemporalType.DATE)
                                       .setParameter("endDate", end, TemporalType.DATE)
                                       .setParameter("priority", priority);
        return query.getResultList();
    }

    @Override
    public List<Incident> getDateRangeIncidentsByApplicationStatus(Date start, Date end, String applicationStatus) {
        TypedQuery<Incident> query = em.createNamedQuery(Incident.FIND_ALL_OPEN_INCIDENTS_BY_RANGE_AND_APPLICATIONSTATUS_RELATIONS, Incident.class)
                                       .setParameter("startDate", start, TemporalType.DATE)
                                       .setParameter("endDate", end, TemporalType.DATE)
                                       .setParameter("applicationStatus", applicationStatus);
        return query.getResultList();
    }

    @Override
    public IncidentGroup getGroup(Long id) {
        Incident incident = em.find(Incident.class, id);
        Optional<IncidentGroup> incidentGroup = Optional.of(incident.getIncidentGroup());
        return incidentGroup.orElse(null);
    }

    @Override
    public Set<IncidentGroup> getGroups() {
        @SuppressWarnings("unchecked")
        List<IncidentGroup> myResult = em.createNamedQuery(Incident.FIND_ALL_GROUPS).getResultList();
        Set<IncidentGroup> groups = new HashSet<IncidentGroup>(myResult);
        return groups;
    }

    @Override
    public Set<IncidentChronology> getChronologies(Long id) {
        Incident incident = em.find(Incident.class, id);
        Optional<Set<IncidentChronology>> chronologies = Optional.of(incident.getChronologies());
        return chronologies.orElse(new HashSet<>());
    }

    @Override
    public Set<Product> getProducts(Long id) {
        Incident incident = em.find(Incident.class, id);
        Optional<Set<Product>> products = Optional.of(incident.getProducts());
        return products.orElse(new HashSet<>());
    }

    @Override
    public List<Incident> getOpenIncidents() {
        String status = "Open";
        TypedQuery<Incident> query = em.createNamedQuery(Incident.FIND_ALL_OPEN_INCIDENTS_RELATIONS, Incident.class).setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public ErrorCondition getErrorCode(Long id) {
        Incident incident = em.find(Incident.class, id);
        Optional<ErrorCondition> errorCode = Optional.of(incident.getError());
        return errorCode.orElse(null);
    }

    @Override
    public ReferenceData getApplicationStatus(Long id) {
        Incident incident = em.find(Incident.class, id);
        Optional<ReferenceData> rd = Optional.of(incident.getApplicationStatus());
        return rd.orElse(null);
    }

    @Override
    public Optional<Incident> getIncident(Long id) {
        try {
            return Optional.of(em.find(Incident.class, id));
        } catch (NullPointerException  e) {
            return Optional.empty();
        }
    }

    private static Consumer<Incident> lambdaWrapper(Consumer<Incident> consumer) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                LOG.error("IncidentRepositoryImpl::deleteIncident - failure deleting incident id {}, msg {}", i.getId(), e.getMessage());
                throw (e);
            }
        };
    }

    private List<IncidentGroup> CheckIncidentGroup(String name) {
        TypedQuery<IncidentGroup> query = em.createNamedQuery(Incident.FIND_INCIDENT_GROUP, IncidentGroup.class).setParameter("name", name);
        return query.getResultList();
    }

}
