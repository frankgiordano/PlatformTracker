package us.com.plattrk.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import us.com.plattrk.api.model.ReferenceData;

@Repository
public class ReferenceDataRepositoryImpl implements ReferenceDataRepository {

	@PersistenceContext
	private EntityManager em;

    public List<ReferenceData> getReferenceDatasByGroupId(Long groupId){
		TypedQuery<ReferenceData> query = em.createQuery("Select new us.com.plattrk.api.model.ReferenceData(i.id, i.displayName, i.groupId, i.description) from ReferenceData as i  Where i.groupId = :groupId order by i.id", ReferenceData.class);
		try {
			query.setHint("org.hibernate.cacheable", true);
		} catch (Exception a) {
			System.out.println(a.getMessage());
		}	
		List<ReferenceData> result = query.setParameter("groupId", groupId).getResultList();
		return result; 	
    };

    public ReferenceData getReferenceData(Long id){
    	ReferenceData reference = em.find(ReferenceData.class, id);
		return reference;
    }

}
