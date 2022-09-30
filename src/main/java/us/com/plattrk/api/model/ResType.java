package us.com.plattrk.api.model;

import us.com.plattrk.util.CopyUtil;

import javax.persistence.*;

@Entity
@Table(name = "Res_Type")
public class ResType {

    private Long id;
    private String name;

    public ResType() {
    }

    public ResType(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "res_type_id")
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
    public ResType clone() {
        ResType instance = new ResType(name);
        CopyUtil.copyProperties(instance, this);
        return instance;
    }

}
