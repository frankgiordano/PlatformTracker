package us.com.plattrk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.com.plattrk.api.model.PageWrapper;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service(value = "ProductService")
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getProducts() {
        return productRepository.getProducts();
    }

    @Override
    @Transactional
    public PageWrapper<Product> search(Map<String, String> filtersMap) {
        return productRepository.getProductsByCriteria(filtersMap);
    }

    @Override
    public List<Product> getActiveProducts() {
        return productRepository.getActiveProducts();
    }

    @Override
    @Transactional
    public Product deleteProduct(Long id) {
        return productRepository.deleteProduct(id);
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.saveProduct(product);
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        return productRepository.getProduct(id);
    }

}
