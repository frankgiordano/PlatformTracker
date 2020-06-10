package us.com.plattrk.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;
import us.com.plattrk.util.PageWrapper;
import us.com.plattrk.util.RepositoryUtil;

@Repository
public class RCARepositoryImpl implements RCARepository {

    private static final Logger log = LoggerFactory.getLogger(RCARepositoryImpl.class);

    @Autowired
    private RepositoryUtil<RCA> repositoryUtil;

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<RCAVO> getRCAs() {
        List<RCAVO> myResult = em.createNamedQuery(RCA.FIND_ALL_RCAS).getResultList();
        return myResult;
    }

    @Override
    public PageWrapper<RCA> getRCAsByCriteria(Map<String, String> filtersMap) {
        String grpName = filtersMap.get("grpName");
        String desc = filtersMap.get("desc");
        Long pageIndex = Long.parseLong(filtersMap.get("pageIndex"));

        boolean isGrpNameEmpty = "*".equals(grpName);
        boolean isDescEmpty = "*".equals(desc);
        grpName = repositoryUtil.appendWildCard(grpName);
        desc = repositoryUtil.appendWildCard(desc);

        Query query;
        List<RCA> result;
        Long total;
        if (isGrpNameEmpty && isDescEmpty) {
            query = em.createNamedQuery(RCA.FIND_ALL_RCAS);
            result = repositoryUtil.criteriaResults(pageIndex, query);
            query = em.createNamedQuery(RCA.FIND_ALL_RCAS_COUNT);
            total = (long) query.getSingleResult();
        } else if (!isGrpNameEmpty && isDescEmpty) {
            query = em.createNamedQuery(RCA.FIND_ALL_RCAS_BY_GRPNAME_CRITERIA)
                      .setParameter("grpName", grpName);
            result = repositoryUtil.criteriaResults(pageIndex, query);
            query = em.createNamedQuery(RCA.FIND_ALL_RCAS_COUNT_BY_GRPNAME_CRITERIA)
                      .setParameter("grpName", grpName);
            total = (long) query.getSingleResult();
        } else if (isGrpNameEmpty) {
            query = em.createNamedQuery(RCA.FIND_ALL_RCAS_BY_DESC_CRITERIA)
                      .setParameter("desc", desc);
            result = repositoryUtil.criteriaResults(pageIndex, query);
            query = em.createNamedQuery(RCA.FIND_ALL_RCAS_COUNT_BY_DESC_CRITERIA)
                      .setParameter("desc", desc);
            total = (long) query.getSingleResult();
        } else {
            query = em.createNamedQuery(RCA.FIND_ALL_RCAS_BY_BOTH_CRITERIA)
                      .setParameter("grpName", grpName)
                      .setParameter("desc", desc);
            result = repositoryUtil.criteriaResults(pageIndex, query);
            query = em.createNamedQuery(RCA.FIND_ALL_RCAS_COUNT_BY_BOTH_CRITERIA)
                      .setParameter("grpName", grpName)
                      .setParameter("desc", desc);
            total = (long) query.getSingleResult();
        }

        return new PageWrapper<RCA>(result, total);
    }

    @Override
    public RCA deleteRCA(Long id) {
        Optional<RCA> rootCause = Optional.of(em.find(RCA.class, id));
        rootCause.ifPresent(lambdaWrapper(rc -> {
            em.remove(rc);
            em.flush();
        }));

        return rootCause.orElse(null);
    }

    @Override
    public RCA saveRCA(RCA rca) {
        try {
            if (rca.getId() == null) {
                em.persist(rca);
                em.flush();
            } else {
                em.merge(rca);
            }
        } catch (PersistenceException e) {
            log.error("RCARepositoryImpl::saveRCA - failure saving root cause = " + rca.toString() + ", msg = " + e.getMessage());
            throw (e);
        }

        return rca;
    }

    @Override
    public RCA getRCA(Long id) {
        RCA incidentRCA = em.find(RCA.class, id);
        return incidentRCA;
    }

    private static Consumer<RCA> lambdaWrapper(Consumer<RCA> consumer) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                log.error("RCARepositoryImpl::deleteRCA - failure deleting root cause id " + i.getId() + ", msg = " + e.getMessage());
                throw (e);
            }
        };
    }

}
