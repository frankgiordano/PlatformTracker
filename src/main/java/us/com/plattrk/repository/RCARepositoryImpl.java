package us.com.plattrk.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;

@Repository
public class RCARepositoryImpl implements RCARepository {

    private static Logger log = LoggerFactory.getLogger(RCARepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

	// public Set<RCA> getRCAsNew() {
	// 	@SuppressWarnings("rawtypes")
		
	// 	List<RCA> myResult = em.createNamedQuery(RCA.FIND_ALL_RCAS)
	// 	        .setHint("javax.persistence.fetchgraph", em.getEntityGraph("rcaWithIncidentGroups"))
	// 			.getResultList();

	// 	Set<RCA> rcas = new HashSet<RCA>(myResult);
	// 	return rcas;
	// }

    @Override
    public List<RCAVO> getRCAs() {
        List<RCAVO> myResult = em.createNamedQuery(RCA.FIND_ALL_RCAS).getResultList();
        return myResult;
    }

    @Override
    public RCA deleteRCA(Long id) {
        RCA rca = null;
        try {
            rca = em.find(RCA.class, id);
            em.remove(rca);
            em.flush();
        } catch (PersistenceException e) {
            log.error("RCARepositoryImpl::deleteRCA - failure deleting root cause id " + id + ", msg = " + e.getMessage());
            throw (e);
        }

        return rca;
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

}
