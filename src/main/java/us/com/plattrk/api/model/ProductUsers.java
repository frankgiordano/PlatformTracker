package us.com.plattrk.api.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Product_Users")
public class ProductUsers {

    private Long id;
    private Date recordingDate;
    private int clients;
    private int endUsers;
    private Product product;

    public ProductUsers() {
    }

    public ProductUsers(Date recordingDate, int clients, int endUsers) {
        this.recordingDate = recordingDate;
        this.clients = clients;
        this.endUsers = endUsers;
    }

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "recording_date", nullable = true)
    public Date getRecordingDate() {
        return recordingDate;
    }

    public void setRecordingDate(Date recordingDate) {
        this.recordingDate = recordingDate;
    }

    @Column(name = "clients", nullable = true)
    public int getClients() {
        return clients;
    }

    public void setClients(int clients) {
        this.clients = clients;
    }

    @Column(name = "end_users", nullable = true)
    public int getEndUsers() {
        return endUsers;
    }

    public void setEndUsers(int endUsers) {
        this.endUsers = endUsers;
    }

    @ManyToOne
    @JoinColumn(name = "product_id")
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}