package us.com.plattrk.api.model;

import javax.persistence.*;

@Entity
@Table(name = "Platform")
public class Platform {
    
    private Long id;
    private String name;
    
    public Platform() {
    }
    
    public Platform(String name) {
        this.name = name;
    }
    
    @Id
    @Column(name = "platform_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", columnDefinition="VARCHAR(50)", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
