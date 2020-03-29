package us.com.plattrk.api.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "Product")
@Cacheable(true)
@NamedQueries({
    @NamedQuery(name = Product.FIND_ALL_PRODUCTS_RELATIONS, 
                query = "Select p from Product p", 
                hints={@QueryHint(name="org.hibernate.cacheable", value="true")}),
    @NamedQuery(name = Product.FIND_ALL_PRODUCTS, 
                query = "Select new us.com.plattrk.api.model.Product(i.id, i.incidentName, i.clientName, i.shortName, i.owner, i.startDate, i.endDate, i.maxWeeklyUptime, i.platform, i.revenue, i.users) from Product as i",
                hints={@QueryHint(name="org.hibernate.cacheable", value="true")}),
    @NamedQuery(name = Product.FIND_ALL_ACTIVE_PRODUCTS, 
                query = "Select new us.com.plattrk.api.model.Product(i.id, i.incidentName, i.clientName, i.shortName, i.owner, i.startDate, i.endDate, i.maxWeeklyUptime, i.platform, i.revenue, i.users) from Product as i where i.endDate IS NULL",
                hints={@QueryHint(name="org.hibernate.cacheable", value="true")}),
})
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Product.class)
public class Product {
    
    public static final String FIND_ALL_PRODUCTS_RELATIONS = "findAllProductsRelations";
    public static final String FIND_ALL_PRODUCTS = "findAllProducts";
    public static final String FIND_ALL_ACTIVE_PRODUCTS = "findAllActiveProducts";
    
    private Long id;
    private String incidentName;
    private String clientName;
    private String shortName;
    private String owner;
    private Date startDate;
    private Date endDate;
    private int maxWeeklyUptime;
    private Double revenue;
    private Double users;
    private Set<ProductUsers> productUsers = new HashSet<ProductUsers>();
    private Set<ProductComponent> productComponents = new HashSet<ProductComponent>();
    private String platform;
    
    public Product() {
    }

    public Product(Long id, String incidentName, String clientName,
            String shortName, String owner, Date startDate, Date endDate,
            int maxWeeklyUptime, String platform, Double revenue, Double users) {
        this.id = id;
        this.incidentName = incidentName;
        this.clientName = clientName;
        this.shortName = shortName;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxWeeklyUptime = maxWeeklyUptime;
        this.revenue = revenue;
        this.users = users;
        this.platform = platform;
    }
    
    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "incident_name", columnDefinition="VARCHAR(50)" , nullable = false)
    public String getIncidentName() {
        return incidentName;
    }

    public void setIncidentName(String incidentName) {
        this.incidentName = incidentName;
    }

    @Column(name = "client_name", columnDefinition="VARCHAR(50)", nullable = false)
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Column(name = "short_name", columnDefinition="VARCHAR(10)", nullable = false)
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(name = "owner", columnDefinition="VARCHAR(50)", nullable = true)
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date",  nullable = false)
    @JsonDeserialize(using=JsonDateMinusTimeDeserializer.class)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)	
    @Column(name = "end_date",  nullable = true)
    @JsonDeserialize(using=JsonDateMinusTimeDeserializer.class)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "max_weekly_uptime", nullable = false)
    public int getMaxWeeklyUptime() {
        return maxWeeklyUptime;
    }

    public void setMaxWeeklyUptime(int maxWeeklyUptime) {
        this.maxWeeklyUptime = maxWeeklyUptime;
    }

    @Column(name = "revenue", columnDefinition="Decimal(10,3) default '0'", nullable = true)
    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    @Column(name = "users", columnDefinition="Decimal(10,3) default '0'", nullable = true)
    public Double getUsers() {
        return users;
    }

    public void setUsers(Double users) {
        this.users = users;
    }

    @org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    public Set<ProductUsers> getProductUsers() {
        return productUsers;
    }

    public void setProductUsers(Set<ProductUsers> productUsers) {
        this.productUsers = productUsers;
    }

    @org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    public Set<ProductComponent> getProductComponents() {
        return productComponents;
    }

    public void setProductComponents(Set<ProductComponent> productComponents) {
        this.productComponents = productComponents;
    }

    @Column(name = "platform", columnDefinition="VARCHAR(50)", nullable = false)
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", incidentName='" + incidentName + '\'' +
                ", clientName='" + clientName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", owner='" + owner + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", maxWeeklyUptime=" + maxWeeklyUptime +
                ", revenue=" + revenue +
                ", users=" + users +
                ", platform='" + platform + '\'' +
                '}';
    }

}
