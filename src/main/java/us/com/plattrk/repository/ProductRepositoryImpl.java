package us.com.plattrk.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Product> getProducts() {
        List<Product> myResult = em.createNamedQuery(Product.FIND_ALL_PRODUCTS).getResultList();
        return myResult;
    }

    @Override
    public boolean deleteProduct(Long id) {
        try {
            Product product = em.find(Product.class, id);
            em.remove(product);
            em.flush();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean saveProduct(Product product) {
        if (product.getId() == null) {
            em.persist(product);
            em.flush();
        } else {
            em.merge(product);
        }
        return true;

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

}
