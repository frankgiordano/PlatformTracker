package us.com.plattrk.api.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
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

import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
//@Table(name = "Incident_Group", uniqueConstraints = {
//	    @UniqueConstraint(columnNames = { "name" })
//	})
@Table(name = "Incident_Group")
@Cacheable(true)
@NamedQueries({
	@NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS_RELATIONS, 
				query = "Select g from IncidentGroup g", 
				hints={@QueryHint(name="org.hibernate.cacheable", value="false")}),
	@NamedQuery(name = IncidentGroup.FIND_ALL_INCIDENT_GROUPS,
				query = "Select new us.com.plattrk.api.model.IncidentGroup(g.id, g.name, g.description, g.status) from IncidentGroup as g",
				hints={@QueryHint(name="org.hibernate.cacheable", value="false")})
})
public class IncidentGroup  {
	
	public static final String FIND_ALL_INCIDENT_GROUPS_RELATIONS = "findAllIncidentGroupsRelations";
	public static final String FIND_ALL_INCIDENT_GROUPS = "findAllIncidentGroups";
	
	private Long id;
	private String name;
	private String description;
	private String status;
	private Set<Incident> incidents = new HashSet<Incident>();
//	private String incidentGroup;
	private Set<IncidentResolution> incidentResolutions = new HashSet<IncidentResolution>();
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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	@Column(name = "name", columnDefinition="VARCHAR(120)", nullable = true)
	public String getName() {
		return name;
	}
	@Column(name = "description", columnDefinition="VARCHAR(4000)", nullable = true)
	public String getDescription() {
		return description;
	}
	@Column(name = "status", columnDefinition="VARCHAR(80)", nullable = true)
	public String getStatus() {
		return status;
	}
//	@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@JsonManagedReference
	@OneToMany(mappedBy="incidentGroup", fetch = FetchType.EAGER)
	public Set<Incident> getIncidents() {
		return incidents;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setIncidents(Set<Incident> incidents) {
		this.incidents = incidents;
	}
}
