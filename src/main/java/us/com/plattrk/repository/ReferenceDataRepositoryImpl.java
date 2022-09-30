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

    public List<ReferenceData> getReferenceDataByGroupId(Long groupId) {
        TypedQuery<ReferenceData> query = em.createNamedQuery(ReferenceData.FIND_REFERENCES_BY_GROUP_ID, ReferenceData.class);
        return query.setParameter("groupId", groupId).getResultList();
    }

    public ReferenceData getReferenceData(Long id) {
        ReferenceData reference = em.find(ReferenceData.class, id);
        return reference;
    }

}
