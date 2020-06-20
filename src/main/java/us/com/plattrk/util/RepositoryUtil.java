package us.com.plattrk.util;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;
import java.util.Map;

@Component(value = "RepositoryUtil")
public class RepositoryUtil<T> {

    private static final int PAGE_SIZE = 10;

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<T> criteriaResults(Long pageIndex, Query query) {
        List<T> result;

        query.setFirstResult((int) ((pageIndex - 1) * PAGE_SIZE));
        query.setMaxResults(PAGE_SIZE);
        result = query.getResultList();

        return result;
    }

    public String appendWildCard(String value) {
        return "%" + value + "%";
    }

    public QueryResult<T> getQueryResult(boolean isOwnerEmpty, String owner, Map<String, String> columnInfo,
                                         long pageIndex, String queryName, String queryCountName, String type) {
        QueryResult<T> qr = new QueryResult<T>();
        Query query;

        if (!isOwnerEmpty) {
            query = createQueryWithOwner(queryName, type);
            setQueryColumnParameters(columnInfo, query);
            query.setParameter("owner", owner);
            qr.result = this.criteriaResults(pageIndex, query);
            query = createQueryWithOwner(queryCountName, type);
            setQueryColumnParameters(columnInfo, query);
            query.setParameter("owner", owner);
        } else {
            query = em.createNamedQuery(queryName);
            setQueryColumnParameters(columnInfo, query);
            qr.result = this.criteriaResults(pageIndex, query);
            query = em.createNamedQuery(queryCountName);
            setQueryColumnParameters(columnInfo, query);
        }
        qr.total = (long) query.getSingleResult();

        return qr;
    }

    private Query createQueryWithOwner(String url, String type) {
        Query query = em.createNamedQuery(url);
        String qlQuery = this.getSqlStr(query);
        int index = qlQuery.indexOf("order");

        StringBuilder newQueryStr = new StringBuilder();
        if (index == -1) {
            newQueryStr.append(qlQuery);
            setAdditionalQueryClause(newQueryStr, type);
        } else {
            newQueryStr.append(qlQuery, 0, index);
            setAdditionalQueryClause(newQueryStr, type);
            newQueryStr.append(qlQuery.substring(index));
        }

        return em.createQuery(newQueryStr.toString());
    }

    private void setQueryColumnParameters(Map<String, String> columnInfo, Query query) {
        for (Map.Entry<String, String> entry : columnInfo.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }

    private void setAdditionalQueryClause(StringBuilder newQueryStr, String type) {
        boolean foundWhere = newQueryStr.toString().contains("where");
        String clause;
        switch (type.toLowerCase()) {
            case "incident":
                clause = "lower(i.owner) = (:owner) ";
                if (!foundWhere) {
                    newQueryStr.append(" where " + clause);
                } else {
                    newQueryStr.append(" and " + clause);
                }
                break;
            case "resolution":
                clause = "lower(res.owner) = (:owner) ";
                if (!foundWhere) {
                    newQueryStr.append(" where " + clause);
                } else {
                    newQueryStr.append(" and " + clause);
                }
                break;
            case "rca":
                clause = "lower(rc.owner) = (:owner) ";
                if (!foundWhere) {
                    newQueryStr.append(" where " + clause);
                } else {
                    newQueryStr.append(" and " + clause);
                }
                break;
            case "project":
                clause = "lower(pr.owner) = (:owner) ";
                if (!foundWhere) {
                    newQueryStr.append(" where " + clause);
                } else {
                    newQueryStr.append(" and " + clause);
                }
                break;
            case "product":
                clause = "lower(pd.owner) = (:owner) ";
                if (!foundWhere) {
                    newQueryStr.append(" where " + clause);
                } else {
                    newQueryStr.append(" and " + clause);
                }
                break;
        }
    }

    private String getSqlStr(Query query) {
        return query.unwrap(org.hibernate.Query.class).getQueryString();
    }

}
