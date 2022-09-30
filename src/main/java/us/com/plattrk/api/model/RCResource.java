package us.com.plattrk.api.model;

import us.com.plattrk.util.CopyUtil;

import javax.persistence.*;

@Entity
@Table(name = "RC_Resource")
public class RCResource {

    private Long id;
    private String name;

    public RCResource() {
    }

    public RCResource(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "rc_resource_id")
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
    public RCResource clone() {
        RCResource instance = new RCResource(name);
        CopyUtil.copyProperties(instance, this);
        return instance;
    }

}