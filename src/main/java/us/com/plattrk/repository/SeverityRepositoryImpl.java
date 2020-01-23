package us.com.plattrk.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.Severity;

@Repository
public class SeverityRepositoryImpl implements SeverityRepository {
	
    @PersistenceContext
	private EntityManager em;

	@Override
	public List<Severity> getSeverities() {
		Query query = em.createQuery("Select i from Severity i");

		@SuppressWarnings({ "unchecked" })
	    List<Severity> myResult = query.getResultList();
		return myResult;
	}

}
