package us.com.plattrk.repository;

import org.springframework.stereotype.Repository;
import us.com.plattrk.api.model.ErrorCondition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

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
