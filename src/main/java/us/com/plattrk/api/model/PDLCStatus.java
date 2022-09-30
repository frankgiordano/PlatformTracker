package us.com.plattrk.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import us.com.plattrk.util.CopyUtil;

@Entity
@Table(name = "PDLC_Status")
public class PDLCStatus {
    
    private Long id;
    private String name;
    
    public PDLCStatus() {
    }
    
    public PDLCStatus(String name) {
        this.name = name;
    }
    
    public PDLCStatus(Long id , String name) {
        this.id = id;
        this.name = name;
    }	
    
    @Id
    @Column(name = "pdlcs_status_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", columnDefinition="VARCHAR(50)" , nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    // ====== Cloneable Override =========

    @Override
    public PDLCStatus clone() {
        PDLCStatus instance= new PDLCStatus(name);
        CopyUtil.copyProperties(instance, this);
        return instance;		
    }

}
