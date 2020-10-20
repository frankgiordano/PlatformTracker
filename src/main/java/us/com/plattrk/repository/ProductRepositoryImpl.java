package us.com.plattrk.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.Product;
import us.com.plattrk.api.model.PageWrapper;
import us.com.plattrk.api.model.QueryResult;
import us.com.plattrk.util.RepositoryUtil;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ProductRepositoryImpl.class);
    private static final String TYPE = "Product";

    @Autowired
    private RepositoryUtil<Product> repositoryUtil;

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<Product> getProducts() {
        List<Product> myResult = em.createNamedQuery(Product.FIND_ALL_PRODUCTS).getResultList();
        return myResult;
    }

    @Override
    public PageWrapper<Product> getProductsByCriteria(Map<String, String> filtersMap) {
        String name = filtersMap.get("name");
        String owner = filtersMap.get("assignee");
        Long pageIndex = Long.parseLong(filtersMap.get("pageIndex"));

        boolean isNameEmpty = "*".equals(name);
        boolean isOwnerEmpty = "*".equals(owner) || "undefined".equals(owner);
        name = repositoryUtil.appendWildCard(name);

        QueryResult<Product> queryResult;
        Map<String, String> columnInfo = new HashMap<>();

        if (isNameEmpty) {
            String queryName = Product.FIND_ALL_PRODUCTS;
            String queryCountName = Product.FIND_ALL_PRODUCTS_COUNT;
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        } else {
            String queryName = Product.FIND_ALL_PRODUCTS_BY_CRITERIA;
            String queryCountName = Product.FIND_ALL_PRODUCTS_COUNT_BY_CRITERIA;
            columnInfo.put("name", name);
            queryResult = repositoryUtil.getQueryResult(isOwnerEmpty, owner, columnInfo, pageIndex, queryName, queryCountName, TYPE);
        }

        return new PageWrapper<Product>(queryResult.result, queryResult.total);
    }

    @Override
    public Product deleteProduct(Long id) {
        Optional<Product> product = Optional.of(em.find(Product.class, id));
        product.ifPresent(lambdaWrapper(p -> {
            em.remove(p);
            em.flush();
        }));

        return product.orElse(null);
    }

    @Override
    public Product saveProduct(Product product) {
        try {
            if (product.getId() == null) {
                em.persist(product);
                em.flush();
            } else {
                em.merge(product);
            }
        } catch (PersistenceException e) {
            LOG.error("ProductRepositoryImpl::saveProduct - failure saving product {}, msg {}", product.toString(), e.getMessage());
            throw (e);
        }

        return product;
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        try {
            return Optional.of(em.find(Product.class, id));
        } catch (NullPointerException  e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> getActiveProducts() {
        TypedQuery<Product> query = em.createNamedQuery(Product.FIND_ALL_ACTIVE_PRODUCTS, Product.class);
        List<Product> myResult = query.getResultList();
        return myResult;
    }

    private static Consumer<Product> lambdaWrapper(Consumer<Product> consumer) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                LOG.error("ProductRepositoryImpl::deleteProduct - failure deleting product id {}, msg {}", i.getId(), e.getMessage());
                throw (e);
            }
        };
    }

}
