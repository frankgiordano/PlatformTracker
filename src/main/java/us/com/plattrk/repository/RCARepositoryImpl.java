package us.com.plattrk.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;

@Repository
public class RCARepositoryImpl implements RCARepository {

	@PersistenceContext
	private EntityManager em;

/*	public Set<RCA> getRCAsNew() {
		@SuppressWarnings("rawtypes")
		
		List<RCA> myResult = em.createNamedQuery(RCA.FIND_ALL_RCAS)
		        .setHint("javax.persistence.fetchgraph", em.getEntityGraph("rcaWithIncidentGroups"))
				.getResultList();

		Set<RCA> rcas = new HashSet<RCA>(myResult);
		return rcas;
	}
*/

	@Override
	public Set<RCAVO> getRCAs() {
		@SuppressWarnings("rawtypes")
		List<RCAVO> myResult = em.createNamedQuery(RCA.FIND_ALL_RCAS).getResultList();

		Set<RCAVO> rcas = new HashSet<RCAVO>(myResult);
		return rcas;
	}

	@Override
	public Set<RCA> getRCAs(RCA rca) {
		return null;
	}

	@Override
	public boolean deleteRCA(Long id) {
		try {
			RCA rca = em.find(RCA.class, id);

			em.remove(rca);
			em.flush();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Transactional
	@Override
	public boolean saveRCA(RCA rca) {
		try {
			if (rca.getId() == null) {
				em.persist(rca);
				em.flush();
			} else {
				em.merge(rca);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public RCA getRCA(Long id) {
		RCA incidentRCA = em.find(RCA.class, id);
		return incidentRCA;
	}

}
