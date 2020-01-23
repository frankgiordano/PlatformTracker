package us.com.plattrk.repository;

import java.util.List;
import java.util.Set;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;

public interface ProductRepository {

	    Set<Product> getProducts();

	    boolean deleteProduct(Long id);

	    boolean saveProduct(Product product);

		Product getProduct(Long id);

		Incident getIncident(Long id);

		List<Product> getActiveProducts();
	
}
