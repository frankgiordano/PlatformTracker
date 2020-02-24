package us.com.plattrk.repository;

import java.util.List;
import java.util.Set;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;

public interface ProductRepository {

    public Set<Product> getProducts();

	public boolean deleteProduct(Long id);

	public boolean saveProduct(Product product);

	public Product getProduct(Long id);

	public Incident getIncident(Long id);

	public List<Product> getActiveProducts();

}
