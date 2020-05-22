package us.com.plattrk.api.model;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "reference_data")
@Cacheable(true)
@NamedQueries({
        @NamedQuery(name = ReferenceData.FIND_REFERENCES_BY_GROUP_ID,
                query = "Select new us.com.plattrk.api.model.ReferenceData(rc.id, rc.displayName, rc.groupId, rc.description) from ReferenceData as rc where rc.groupId = :groupId order by rc.id",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
})
public class ReferenceData implements Serializable {

    public static final String FIND_REFERENCES_BY_GROUP_ID = "findReferencesByGroupId";

    private Long id;
    private String description;
    private String displayName;
    private Long groupId;

    public ReferenceData() {
    }

    public ReferenceData(Long id, String displayName, Long groupId, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.groupId = groupId;
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

    @Column(name = "group_id", nullable = true)
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "ReferenceData{" + "description='" + description + '\'' + ", displayName='" + displayName + '\''
                + ", groupId=" + groupId + ", id=" + id + '}';
    }

}