package us.com.plattrk.repository;

import us.com.plattrk.api.model.PageWrapper;
import us.com.plattrk.api.model.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductRepository {

    public List<Product> getProducts();

    public PageWrapper<Product> getProductsByCriteria(Map<String, String> filtersMap);

    public Product deleteProduct(Long id);

    public Product saveProduct(Product product);

    public Optional<Product> getProduct(Long id);

    public List<Product> getActiveProducts();

}
