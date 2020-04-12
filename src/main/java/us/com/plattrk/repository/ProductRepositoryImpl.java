package us.com.plattrk.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private static Logger log = LoggerFactory.getLogger(ProductRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Product> getProducts() {
        List<Product> myResult = em.createNamedQuery(Product.FIND_ALL_PRODUCTS).getResultList();
        return myResult;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Incident getIncident(Long id) {
        // TODO Auto-generated method stub
        return null;
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
