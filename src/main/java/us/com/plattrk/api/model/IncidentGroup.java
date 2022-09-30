package us.com.plattrk.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Incident_Group")
@Cacheable(true)
@NamedQueries({
        @NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS_RELATIONS,
                query = "Select g from IncidentGroup g",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS,
                query = "Select new us.com.plattrk.api.model.IncidentGroup(g.id, g.name, g.description, g.status) from IncidentGroup as g order by g.name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS_BY_NAME_CRITERIA,
                query = "Select new us.com.plattrk.api.model.IncidentGroup(g.id, g.name, g.description, g.status) from IncidentGroup as g " +
                        "where lower(g.name) LIKE (:name) order by g.name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS_BY_DESC_CRITERIA,
                query = "Select new us.com.plattrk.api.model.IncidentGroup(g.id, g.name, g.description, g.status) from IncidentGroup as g " +
                        "where lower(g.description) LIKE (:desc) order by g.name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS_BY_BOTH_CRITERIA,
                query = "Select new us.com.plattrk.api.model.IncidentGroup(g.id, g.name, g.description, g.status) from IncidentGroup as g " +
                        "where lower(g.name) LIKE (:name) and lower(g.description) LIKE (:desc) order by g.name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS_COUNT_BY_NAME_CRITERIA,
                query = "Select count(g.id) from IncidentGroup as g where lower(g.name) LIKE (:name)",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS_COUNT_BY_DESC_CRITERIA,
                query = "Select count(g.id) from IncidentGroup as g where lower(g.description) LIKE (:desc)",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS_COUNT_BY_BOTH_CRITERIA,
                query = "Select count(g.id) from IncidentGroup as g where lower(g.name) LIKE (:name) and lower(g.description) LIKE (:desc)",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS_COUNT,
                query = "Select count(g.id) from IncidentGroup as g",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")})
})
public class IncidentGroup {

    public static final String FIND_ALL_INCIDENT_GROUPS_RELATIONS = "findAllIncidentGroupsRelations";
    public static final String FIND_ALL_INCIDENT_GROUPS = "findAllIncidentGroups";
    public static final String FIND_ALL_INCIDENT_GROUPS_BY_NAME_CRITERIA = "findAllIncidentGroupsByNameCriteria";
    public static final String FIND_ALL_INCIDENT_GROUPS_BY_DESC_CRITERIA = "findAllIncidentGroupsByDescCriteria";
    public static final String FIND_ALL_INCIDENT_GROUPS_BY_BOTH_CRITERIA = "findAllIncidentGroupsByBothCriteria";
    public static final String FIND_ALL_INCIDENT_GROUPS_COUNT_BY_NAME_CRITERIA = "findAllIncidentGroupsCountByNameCriteria";
    public static final String FIND_ALL_INCIDENT_GROUPS_COUNT_BY_DESC_CRITERIA = "findAllIncidentGroupsCountByDescCriteria";
    public static final String FIND_ALL_INCIDENT_GROUPS_COUNT_BY_BOTH_CRITERIA = "findAllIncidentGroupsCountByBothCriteria";
    public static final String FIND_ALL_INCIDENT_GROUPS_COUNT = "findAllIncidentGroupsCount";

    private Long id;
    private String name;
    private String description;
    private String status;
    private Set<Incident> incidents = new HashSet<>();

    public IncidentGroup() {
    }

    public IncidentGroup(String name) {
        this.name = name;
    }

    public IncidentGroup(Long id) {
        this.id = id;
    }

    public IncidentGroup(Long id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", columnDefinition = "VARCHAR(120)", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", columnDefinition = "VARCHAR(4000)", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "status", columnDefinition = "VARCHAR(80)", nullable = true)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "incidentGroup", fetch = FetchType.EAGER)
    public Set<Incident> getIncidents() {
        return incidents;
    }

    public void setIncidents(Set<Incident> incidents) {
        this.incidents = incidents;
    }

    @Override
    public String toString() {
        return "IncidentGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
