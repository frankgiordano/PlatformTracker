package us.com.plattrk.repository;

import java.util.List;
import java.util.Map;

import us.com.plattrk.api.model.Product;
import us.com.plattrk.util.PageWrapper;

public interface ProductRepository {

    public List<Product> getProducts();

    public PageWrapper<Product> getProductsByCriteria(Map<String, String> filtersMap);

    public Product deleteProduct(Long id);

    public Product saveProduct(Product product);

    public Product getProduct(Long id);

    public List<Product> getActiveProducts();

}
