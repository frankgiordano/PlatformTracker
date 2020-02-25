package us.com.plattrk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.repository.ProductRepository;

@Service(value = "ProductService")
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	private ProductRepository productRepository;

	@Override
	@Transactional
	public List<Product> getProducts() {
		return productRepository.getProducts();
	}

	@Override
	@Transactional
	public boolean deleteProduct(Long id) {
		return productRepository.deleteProduct(id);
	}

	@Override
	@Transactional
	public boolean saveProduct(Product product) {
		return productRepository.saveProduct(product);
	}

	@Override
	@Transactional
	public Product getProduct(Long id) {
		return productRepository.getProduct(id);
	}

	@Override
	@Transactional
	public Incident getIncident(Long id) {
		return productRepository.getIncident(id);
	}

	@Override
	public List<Product> getActiveProducts() {
		return productRepository.getActiveProducts();
	}

}
