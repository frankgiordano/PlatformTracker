package us.com.plattrk.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;

@Repository
public class RCARepositoryImpl implements RCARepository {

    private static Logger log = LoggerFactory.getLogger(RCARepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<RCAVO> getRCAs() {
        List<RCAVO> myResult = em.createNamedQuery(RCA.FIND_ALL_RCAS).getResultList();
        return myResult;
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
