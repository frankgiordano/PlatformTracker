package us.com.plattrk.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import us.com.plattrk.api.model.Product;
import us.com.plattrk.api.model.PageWrapper;

public interface ProductService {

    public List<Product> getProducts();

    public PageWrapper<Product> search(Map<String, String> filtersMap);

    public List<Product> getActiveProducts();

    public Product deleteProduct(Long id);

    public Product saveProduct(Product product);

    public Optional<Product> getProduct(Long id);

}
