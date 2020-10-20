package us.com.plattrk.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;
import us.com.plattrk.api.model.PageWrapper;
import us.com.plattrk.api.model.QueryResult;
import us.com.plattrk.util.RepositoryUtil;

@Repository
public class RCARepositoryImpl implements RCARepository {

    private static final Logger LOG = LoggerFactory.getLogger(RCARepositoryImpl.class);

    @Autowired
    private RepositoryUtil<RCA> repositoryUtil;
    private static final String TYPE = "RCA";

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
        String owner = filtersMap.get("assignee");
        Long pageIndex = Long.parseLong(filtersMap.get("pageIndex"));

        boolean isGrpNameEmpty = "*".equals(grpName);
        boolean isDescEmpty = "*".equals(desc);
        boolean isOwnerEmpty = "*".equals(owner) || "undefined".equals(owner);
        grpName = repositoryUtil.appendWildCard(grpName);
        desc = repositoryUtil.appendWildCard(desc);

        QueryResult<RCA> queryResult;
        Map<String, String> columnInfo = new HashMap<>();

        if (isGrpNameEmpty && isDescEmpty) {
            String queryName = RCA.FIND_ALL_RCAS;
            String queryCountName = RCA.FIND_ALL_RCAS_COUNT;
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else if (!isGrpNameEmpty && isDescEmpty) {
            String queryName = RCA.FIND_ALL_RCAS_BY_GRPNAME_CRITERIA;
            String queryCountName = RCA.FIND_ALL_RCAS_COUNT_BY_GRPNAME_CRITERIA;
            columnInfo.put("grpName", grpName);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else if (isGrpNameEmpty) {
            String queryName = RCA.FIND_ALL_RCAS_BY_DESC_CRITERIA;
            String queryCountName = RCA.FIND_ALL_RCAS_COUNT_BY_DESC_CRITERIA;
            columnInfo.put("desc", desc);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else {
            String queryName = RCA.FIND_ALL_RCAS_BY_BOTH_CRITERIA;
            String queryCountName = RCA.FIND_ALL_RCAS_COUNT_BY_BOTH_CRITERIA;
            columnInfo.put("grpName", grpName);
            columnInfo.put("desc", desc);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        }

        return new PageWrapper<RCA>(queryResult.result, queryResult.total);
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
            LOG.error("RCARepositoryImpl::saveRCA - failure saving root cause {}, msg {}", rca.toString(), e.getMessage());
            throw (e);
        }

        return rca;
    }

    @Override
    public Optional<RCA> getRCA(Long id) {
        try {
            return Optional.of(em.find(RCA.class, id));
        } catch (NullPointerException  e) {
            return Optional.empty();
        }
    }

    private static Consumer<RCA> lambdaWrapper(Consumer<RCA> consumer) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                LOG.error("RCARepositoryImpl::deleteRCA - failure deleting root cause id {}, msg {}", i.getId(), e.getMessage());
                throw (e);
            }
        };
    }

}
