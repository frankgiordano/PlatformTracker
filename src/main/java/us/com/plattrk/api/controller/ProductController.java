package us.com.plattrk.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.service.ProductService;

@RestController
@RequestMapping(value = "/product")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/products/retrieve", method = RequestMethod.GET, produces = "application/json")
	public List<Product> getProducts() {
		return productService.getProducts();
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/products/retrieveactive", method = RequestMethod.GET, produces = "application/json")
	public List<Product> getActiveProducts() {
		return productService.getActiveProducts();
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/retrieve/{id}", method = RequestMethod.GET, produces = "application/json")
	public Product getProduct(@PathVariable Long id) {
		return productService.getProduct(id);
	}

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public boolean deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public boolean saveProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }
    
    @PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/retrieve/incident/{id}", method = RequestMethod.GET, produces = "application/json")
	public Incident getIncident(@PathVariable Long id) {
		return productService.getIncident(id);
	}

}