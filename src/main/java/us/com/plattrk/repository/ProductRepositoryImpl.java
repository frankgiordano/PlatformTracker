package us.com.plattrk.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.Product;
import us.com.plattrk.util.PageWrapper;
import us.com.plattrk.util.RepositoryUtil;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private static Logger log = LoggerFactory.getLogger(ProductRepositoryImpl.class);
    private static final int PAGE_SIZE = 10;

    @Autowired
    private RepositoryUtil repositoryUtil;

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Product> getProducts() {
        List<Product> myResult = em.createNamedQuery(Product.FIND_ALL_PRODUCTS).getResultList();
        return myResult;
    }

    @Override
    public PageWrapper<Product> getProductsByCriteria(String searchTerm, Long pageIndex) {
        Long total;
        List<Product> result;
        Query query;

        if (!searchTerm.equals("*")) {
            query = em.createNamedQuery(Product.FIND_ALL_PRODUCTS_BY_CRITERIA).setParameter("name", "%" + searchTerm.toLowerCase() + "%");
            result = repositoryUtil.criteriaResults(pageIndex, query, PAGE_SIZE);
            Query queryTotal = em.createNamedQuery(Product.ALL_PRODUCTS_COUNT_BY_CRITERIA).setParameter("name", "%" + searchTerm.toLowerCase() + "%");
            total = (long) queryTotal.getSingleResult();
        } else {
            query = em.createNamedQuery(Product.FIND_ALL_PRODUCTS);
            result = repositoryUtil.criteriaResults(pageIndex, query, PAGE_SIZE);
            Query queryTotal = em.createNamedQuery(Product.ALL_PRODUCTS_COUNT);
            total = (long) queryTotal.getSingleResult();
        }

        return new PageWrapper<Product>(result, total);
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
            log.error("ProductRepositoryImpl::saveProduct - failure saving product = " + product.toString() + ", msg = " + e.getMessage());
            throw (e);
        }

        return product;
    }

    @Override
    public Product getProduct(Long id) {
        return em.find(Product.class, id);
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
                log.error("ProductRepositoryImpl::deleteProduct - failure deleting product id " + i.getId() + ", msg = " + e.getMessage());
                throw (e);
            }
        };
    }

}
