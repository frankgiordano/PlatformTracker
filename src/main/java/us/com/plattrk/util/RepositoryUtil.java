package us.com.plattrk.util;

import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.List;

@Component(value = "RepositoryUtil")
public class RepositoryUtil<T> {

    private static final int PAGE_SIZE = 10;

    @SuppressWarnings("unchecked")
    public List<T> criteriaResults(Long pageIndex, Query query) {
        List<T> result;

        query.setFirstResult((int) ((pageIndex - 1) * PAGE_SIZE));
        query.setMaxResults(PAGE_SIZE);
        result = query.getResultList();

        return result;
    }

}
