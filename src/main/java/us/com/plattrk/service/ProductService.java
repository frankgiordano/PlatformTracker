package us.com.plattrk.service;

import java.util.List;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;

public interface ProductService {

    public List<Product> getProducts();

    public List<Product> getActiveProducts();

    public boolean deleteProduct(Long id);

    public boolean saveProduct(Product product);

    public Product getProduct(Long id);

    public Incident getIncident(Long id);

}
