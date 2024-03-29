package us.com.plattrk.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.PageWrapper;
import us.com.plattrk.api.model.QueryResult;
import us.com.plattrk.util.RepositoryUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
public class IncidentResolutionRepositoryImpl implements IncidentResolutionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentResolutionRepositoryImpl.class);
    private static final String TYPE = "Resolution";

    @Autowired
    private RepositoryUtil<IncidentResolution> repositoryUtil;

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<IncidentResolution> getResolutions() {
        return (List<IncidentResolution>) em.createNamedQuery(IncidentResolution.FIND_ALL_RESOLUTIONS).getResultList();
    }

    @Override
    public PageWrapper<IncidentResolution> getResolutionsByCriteria(Map<String, String> filtersMap) {
        String grpName = filtersMap.get("grpName");
        String desc = filtersMap.get("desc");
        String owner = filtersMap.get("assignee");
        Long pageIndex = Long.parseLong(filtersMap.get("pageIndex"));

        boolean isGrpNameEmpty = "*".equals(grpName);
        boolean isDescEmpty = "*".equals(desc);
        boolean isOwnerEmpty = "*".equals(owner) || "undefined".equals(owner);
        grpName = repositoryUtil.appendWildCard(grpName);
        desc = repositoryUtil.appendWildCard(desc);

        QueryResult<IncidentResolution> queryResult;
        Map<String, String> columnInfo = new HashMap<>();

        if (isGrpNameEmpty && isDescEmpty) {
            String queryName = IncidentResolution.FIND_ALL_RESOLUTIONS;
            String queryCountName = IncidentResolution.FIND_ALL_RESOLUTIONS_COUNT;
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else if (!isGrpNameEmpty && isDescEmpty) {
            String queryName = IncidentResolution.FIND_ALL_RESOLUTIONS_BY_GRPNAME_CRITERIA;
            String queryCountName = IncidentResolution.FIND_ALL_RESOLUTIONS_COUNT_BY_GRPNAME_CRITERIA;
            columnInfo.put("grpName", grpName);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else if (isGrpNameEmpty) {
            String queryName = IncidentResolution.FIND_ALL_RESOLUTIONS_BY_DESC_CRITERIA;
            String queryCountName = IncidentResolution.FIND_ALL_RESOLUTIONS_COUNT_BY_DESC_CRITERIA;
            columnInfo.put("desc", desc);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else {
            String queryName = IncidentResolution.FIND_ALL_RESOLUTIONS_BY_BOTH_CRITERIA;
            String queryCountName = IncidentResolution.FIND_ALL_RESOLUTIONS_COUNT_BY_BOTH_CRITERIA;
            columnInfo.put("grpName", grpName);
            columnInfo.put("desc", desc);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        }

        return new PageWrapper<>(queryResult.result, queryResult.total);
    }

    @Override
    public IncidentResolution deleteResolution(Long id) {
        Optional<IncidentResolution> resolution = Optional.of(em.find(IncidentResolution.class, id));
        resolution.ifPresent(lambdaWrapper(r -> {
            em.remove(r);
            em.flush();
        }));

        return resolution.orElse(null);
    }

    @Override
    public IncidentResolution saveResolution(IncidentResolution resolution) {
        try {
            em.merge(resolution);
        } catch (PersistenceException e) {
            LOG.error("IncidentResolutionRepositoryImpl::saveResolution - failure saving resolution {}, msg {}", resolution.toString(), e.getMessage());
            throw (e);
        }

        return resolution;
    }

    @Override
    public List<IncidentResolution> saveResolutions(List<IncidentResolution> resolutions) {
        resolutions.forEach(lambdaWrapper(resolution -> em.merge(resolution), resolutions));
        return resolutions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<IncidentResolution> getGroupResolutions(Long id) {
        return (List<IncidentResolution>) em.createNamedQuery(IncidentResolution.FIND_ALL_RESOLUTIONS_PER_GROUP).setParameter("pid", id).getResultList();
    }

    @Override
    public Optional<IncidentResolution> getResolution(Long id) {
        try {
            return Optional.of(em.find(IncidentResolution.class, id));
        } catch (NullPointerException  e) {
            return Optional.empty();
        }
    }

    private static Consumer<IncidentResolution> lambdaWrapper(Consumer<IncidentResolution> consumer, List<IncidentResolution> resolutions) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                LOG.error("IncidentResolutionRepositoryImpl::saveResolutions - failure saving resolution {}, msg {}", i.toString(), e.getMessage());
                resolutions.remove(i);
            }
        };
    }

    private static Consumer<IncidentResolution> lambdaWrapper(Consumer<IncidentResolution> consumer) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                LOG.error("IncidentResolutionRepositoryImpl::deleteResolution - failure deleting resolution id {}, msg {}", i.getId(), e.getMessage());
                throw (e);
            }
        };
    }

}
