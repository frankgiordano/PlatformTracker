package us.com.plattrk.util;

import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.List;

@Component(value = "RepositoryUtil")
public class RepositoryUtil<T> {

    public List<T> criteriaResults(Long pageIndex, Query query, int pageSize) {
        List<T> result;

        query.setFirstResult((int) ((pageIndex - 1) * pageSize));
        query.setMaxResults(pageSize);
        result = query.getResultList();

        return result;
    }

}
