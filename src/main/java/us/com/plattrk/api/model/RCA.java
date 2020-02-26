package us.com.plattrk.api.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import us.com.plattrk.search.Searchable;

@Entity
@Table(name = "RCA")
/*
 * @NamedQueries({
 * 
 * @NamedQuery(name = RCA.FIND_ALL_RCAS, query =
 * "Select new us.com.plattrk.api.model.RCA(i.id, i.owner, i.category, i.resource, i.problem, i.dueDate, i.completionDate, i.status, i.incidentGroup, i.whys) from RCA as i"
 * , hints={@QueryHint(name="org.hibernate.cacheable", value="true")}) })
 */
@NamedQueries({ @NamedQuery(name = RCA.FIND_ALL_RCAS, query = "Select new us.com.plattrk.api.model.RCAVO( i.id, i.owner, c.displayName, re.displayName, i.problem, i.dueDate, i.completionDate, s.displayName, ig.name, i.whys) from RCA as i join i.incidentGroup ig join i.status s join i.category c join i.resource re order by ig.name ") })
/*@NamedQueries({ @NamedQuery(name = RCA.FIND_ALL_RCAS, query = "Select i from RCA as i ") })
@NamedEntityGraphs({
		@NamedEntityGraph(name = "rcaWithIncidentGroups", 
				
				attributeNodes = { @NamedAttributeNode("incidentGroup") 
				
				}),
		@NamedEntityGraph(name = "rcaWithIncidentGroupsAndIncidents", 
		
				attributeNodes = { @NamedAttributeNode(value = "incidentGroup", subgraph = "incidentGroupGraph") }, 
				
				subgraphs = { @NamedSubgraph(name = "incidentGroupGraph", 
				
						attributeNodes = { @NamedAttributeNode("incidents") 
						}) 
				})
		})*/
public class RCA implements Searchable {

	public static final String FIND_ALL_RCAS = "findRCAs";

	private Long id;
	private ReferenceData category;
	private Date completionDate;
	private Date dueDate;
	private IncidentGroup incidentGroup;
	private String owner;
	private String problem;
	private ReferenceData resource;
	private ReferenceData status;
	private String whys;

	public RCA() {
	}

	public RCA(Long id, String owner, ReferenceData category,
			ReferenceData resource, String problem, java.util.Date dueDate,
			java.util.Date completionDate, ReferenceData status,
			IncidentGroup incidentGroup, String whys) {
		this.id = id;
		this.owner = owner;
		this.category = category;
		this.resource = resource;
		this.problem = problem;
		if (dueDate != null)
			this.dueDate = new Date(dueDate.getTime());
		if (completionDate != null)
			this.completionDate = new Date(completionDate.getTime());	
		
		this.status = status;
		this.incidentGroup = incidentGroup;
		this.whys = whys;
	}

	@Id
	@Column(name = "rca_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "rd_category_id", referencedColumnName = "id")
	public ReferenceData getCategory() {
		return category;
	}

	public void setCategory(ReferenceData category) {
		this.category = category;
	}

	@Column(name = "completion_date", nullable = true)
	public Date getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}

	@Column(name = "due_date", nullable = true)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@OneToOne
	@JoinColumn(name = "incident_group_id", referencedColumnName = "group_id", nullable = false)
	public IncidentGroup getIncidentGroup() {
		return incidentGroup;
	}

	public void setIncidentGroup(IncidentGroup incidentGroup) {
		this.incidentGroup = incidentGroup;
	}

	@Column(name = "owner", columnDefinition = "VARCHAR(64)", nullable = false)
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Column(name = "problem", columnDefinition = "VARCHAR(4000)", nullable = true)
	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	@OneToOne
	@JoinColumn(name = "rd_resource_id", referencedColumnName = "id")
	public ReferenceData getResource() {
		return resource;
	}

	public void setResource(ReferenceData resource) {
		this.resource = resource;
	}

	@OneToOne
	@JoinColumn(name = "rd_status_id", referencedColumnName = "id", nullable = false)
	public ReferenceData getStatus() {
		return status;
	}

	public void setStatus(ReferenceData status) {
		this.status = status;
	}

	@Column(name = "whys", columnDefinition = "VARCHAR(4000)", nullable = true)
	public String getWhys() {
		return whys;
	}

	public void setWhys(String whys) {
		this.whys = whys;
	}

	@Override
	public String toString() {
		return "RCA{" +
				"id=" + id +
				", category=" + category +
				", completionDate=" + completionDate +
				", dueDate=" + dueDate +
				", incidentGroup=" + incidentGroup +
				", owner='" + owner + '\'' +
				", problem='" + problem + '\'' +
				", resource=" + resource +
				", status=" + status +
				", whys='" + whys + '\'' +
				'}';
	}

}
