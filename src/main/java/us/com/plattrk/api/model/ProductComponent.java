package us.com.plattrk.api.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Product_Component")
public class ProductComponent {

    private Long Id;
    private String componentName;
    private Product product;
    private Set<ProductComponentRevenue> revenues;

    public ProductComponent() {
    }

    public ProductComponent(String componentName, Set<ProductComponentRevenue> revenues) {
        this.componentName = componentName;
        this.revenues = revenues;
    }

    @Id
    @Column(name = "component_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    @Column(name = "component_name", columnDefinition = "VARCHAR(50)", nullable = true)
    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @ManyToOne
    @JoinColumn(name = "product_id")
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @OneToMany(mappedBy = "component")
    public Set<ProductComponentRevenue> getRevenues() {
        return revenues;
    }

    public void setRevenues(Set<ProductComponentRevenue> revenues) {
        this.revenues = revenues;
    }

}
