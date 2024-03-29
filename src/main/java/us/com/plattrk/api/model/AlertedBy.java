package us.com.plattrk.api.model;

import us.com.plattrk.util.CopyUtil;

import javax.persistence.*;

@Entity
@Table(name = "Alerted_By")
public class AlertedBy {

    private Long id;
    private String name;

    public AlertedBy() {
    }

    public AlertedBy(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "alerted_by_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", columnDefinition = "VARCHAR(50)", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ====== Cloneable Override =========
    @Override
    public AlertedBy clone() {
        AlertedBy instance = new AlertedBy(name);
        CopyUtil.copyProperties(instance, this);
        return instance;
    }

}
