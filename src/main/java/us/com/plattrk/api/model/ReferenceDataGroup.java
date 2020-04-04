package us.com.plattrk.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reference_data_group")
public class ReferenceDataGroup {

    private String description;
    private String displayName;
    private Long id;

    public ReferenceDataGroup() {
    }

    public ReferenceDataGroup(Long id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }

    @Column(name = "description", columnDefinition = "VARCHAR(100)", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "display_name", columnDefinition = "VARCHAR(50)", nullable = true)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}