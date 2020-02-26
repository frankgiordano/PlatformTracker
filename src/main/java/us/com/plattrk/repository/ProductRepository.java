package us.com.plattrk.repository;

import java.util.List;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;

public interface ProductRepository {

    public List<Product> getProducts();

	public Product deleteProduct(Long id);

	public Product saveProduct(Product product);

	public Product getProduct(Long id);

	public Incident getIncident(Long id);

	public List<Product> getActiveProducts();

}
