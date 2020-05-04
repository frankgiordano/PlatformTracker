package us.com.plattrk.service;

import java.util.List;

import us.com.plattrk.api.model.Product;
import us.com.plattrk.util.PageWrapper;

public interface ProductService {

    public List<Product> getProducts();

    public PageWrapper<Product> search(String searchTerm, Long pageIndex);

    public List<Product> getActiveProducts();

    public Product deleteProduct(Long id);

    public Product saveProduct(Product product);

    public Product getProduct(Long id);

}
