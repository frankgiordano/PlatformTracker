package us.com.plattrk.api.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Product_Component_Revenue")
public class ProductComponentRevenue {

    private Long id;
    private Date startDate;
    private Date endDate;
    private int revenue;
    private ProductComponent component;

    public ProductComponentRevenue() {
    }

    public ProductComponentRevenue(Date startDate, Date endDate, int revenue) {
        super();
        this.startDate = startDate;
        this.endDate = endDate;
        this.revenue = revenue;
    }

    @Id
    @Column(name = "revenue_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = true)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date", nullable = true)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "revenue", nullable = true)
    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    @ManyToOne
    @JoinColumn(name = "component_id")
    public ProductComponent getComponent() {
        return component;
    }

    public void setComponent(ProductComponent component) {
        this.component = component;
    }

}
