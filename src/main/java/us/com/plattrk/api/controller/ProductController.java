package us.com.plattrk.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.com.plattrk.api.model.Product;
import us.com.plattrk.service.ProductService;
import us.com.plattrk.util.PageWrapper;

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
    @RequestMapping(value = "/products/retrieve/{searchTerm}/{pageIndex}", method = RequestMethod.GET, produces = "application/json")
    PageWrapper<Product> search(@PathVariable String searchTerm, @PathVariable Long pageIndex) {
        return productService.search(searchTerm, pageIndex);
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
    public Product deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public Product saveProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

}