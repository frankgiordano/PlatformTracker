package us.com.plattrk.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import us.com.plattrk.api.model.ErrorCondition;

@Repository
public class ErrorConditionRepositoryImpl implements ErrorConditionRepository {
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ErrorCondition> getErrorConditions() {
        TypedQuery<ErrorCondition> query = em.createNamedQuery(ErrorCondition.FIND_ALL_ERROR_CONDITIONS, ErrorCondition.class);
        return query.getResultList();
    }

}
