package us.com.plattrk.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	private static Logger log = LoggerFactory.getLogger(IncidentRepositoryImpl.class);

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Set<Product> getProducts() {
		
//		TypedQuery<Product> query = em.createNamedQuery(Product.FIND_ALL_PRODUCTS, Product.class);
//		List<Product> myResult = query.getResultList();
//	
//		Set<Product> products = new HashSet<Product>(myResult);
		
		@SuppressWarnings("unchecked")
		List<Product> myResult = em.createNamedQuery(Product.FIND_ALL_PRODUCTS).getResultList();
		Set<Product> products = new HashSet<Product>(myResult);
		
		return products;
	}

	@Override
	public boolean deleteProduct(Long id) {
		try {
			Product product = em.find(Product.class, id);
//			if (incident.getIncidentGroup() != null)
//				incident.setIncidentGroup(null);
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
		}
		else 
		{
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
