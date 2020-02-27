package us.com.plattrk.api.model;

import java.sql.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Incident_Resolution")
@NamedQueries({
        @NamedQuery(name = IncidentResolution.FIND_ALL_RESOLUTIONS, query = " Select new us.com.plattrk.api.model.IncidentResolution(i.id, i.owner, i.description, i.actualCompletionDate, h.displayName,  i.estCompletionDate, p.name, "
                + " s.displayName, t.displayName, i.sriArtifact,  ig.name, p.id )from IncidentResolution as i"
                + " left outer join i.resolutionProject p inner join i.incidentGroup ig"
                + " inner join i.status s inner join i.horizon h inner join i.type t order by ig.name", hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}),
        @NamedQuery(name = IncidentResolution.FIND_ALL_RESOLUTIONS_PER_GROUP, query = "Select new us.com.plattrk.api.model.IncidentResolution(r.id, r.description, r.actualCompletionDate, h.displayName, r.estCompletionDate) from IncidentResolution as r inner join r.horizon h where r.incidentGroup.id = :pid",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
})
public class IncidentResolution {

    public static final String FIND_ALL_RESOLUTIONS = "findAllResolutions";
    public static final String FIND_ALL_STATUS = "findAllStatus";
    public static final String FIND_ALL_RESOLUTIONS_PER_GROUP = "findAllResolutionsPerGroup";

	private Long id;
    private String description;
    private ReferenceData horizon;
    private String horizonName;
	private Date actualCompletionDate;
    private IncidentGroup incidentGroup;
    private String incidentGroupName;
    private String owner;
    private Long projectId;
    private Project resolutionProject;
    private String resolutionProjectName;
    private String sriArtifact;
    private Date estCompletionDate;
    private ReferenceData status;
    private String statusName;
    private ReferenceData type;
    private String typeName;

    public IncidentResolution() {
        this(null);
    }

    public IncidentResolution(Long id) {
        this.id = id;
    }

    public IncidentResolution(Long id, String description, java.util.Date actualCompletionDate,
                              String horizonName, java.util.Date estCompletionDate) {
        this.description = description;
        this.horizonName = horizonName;
        this.id = id;
        if (actualCompletionDate != null)
            this.actualCompletionDate = new Date(actualCompletionDate.getTime());
        if (estCompletionDate != null)
            this.estCompletionDate = new Date(estCompletionDate.getTime());
    }

    public IncidentResolution(Long id, String owner, String description) {
        this.id = id;
        this.owner = owner;
        this.description = description;
    }

    public IncidentResolution(Long id, String owner, String description,
                              Date actualCompletionDate, ReferenceData horizon,
                              Date estCompletionDate, Project resolutionProject,
                              ReferenceData status, ReferenceData type, String sriArtifact,
                              IncidentGroup incidentGroup) {
        this.id = id;
        this.incidentGroup = incidentGroup;
        this.horizon = horizon;
        this.owner = owner;
        this.description = description;
        this.actualCompletionDate = actualCompletionDate;
        this.estCompletionDate = estCompletionDate;
        this.type = type;
        this.sriArtifact = sriArtifact;
        this.status = status;
        this.resolutionProject = resolutionProject;
    }

    public IncidentResolution(Long id, String owner, String description,
                              java.util.Date actualCompletionDate, String horizonName,
                              java.util.Date estCompletionDate, String resolutionProject,
                              String statusName, String typeName, String sriArtifact,
                              String incidentGroupName, Long projectId) {
        this.id = id;
        this.incidentGroupName = incidentGroupName;
        this.horizonName = horizonName;
        this.owner = owner;
        this.description = description;
        if (actualCompletionDate != null)
            this.actualCompletionDate = new Date(actualCompletionDate.getTime());
        if (estCompletionDate != null)
            this.estCompletionDate = new Date(estCompletionDate.getTime());
        this.typeName = typeName;
        this.sriArtifact = sriArtifact;
        this.statusName = statusName;
        this.resolutionProjectName = resolutionProject;
        this.projectId = projectId;
    }

	@Id
	@Column(name = "resolution_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "description", columnDefinition = "VARCHAR(4000)", nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rd_horizon_id", referencedColumnName = "id", nullable = false)
	public ReferenceData getHorizon() {
		return horizon;
	}

	public void setHorizon(ReferenceData horizon) {
		this.horizon = horizon;
	}

	@Transient
	public String getHorizonName() {
		return horizonName;
	}

	public void setHorizonName(String horizonName) {
		this.horizonName = horizonName;
	}

    // @Temporal(TemporalType.DATE)
    @Column(name = "actual_completion_date", nullable = true)
    public Date getActualCompletionDate() {
        return actualCompletionDate;
    }

	public void setActualCompletionDate(Date actualCompletionDate) {
		this.actualCompletionDate = actualCompletionDate;
	}

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinColumn(name = "group_id", nullable = false)
	public IncidentGroup getIncidentGroup() {
		return incidentGroup;
	}

	public void setIncidentGroup(IncidentGroup incidentGroup) {
		this.incidentGroup = incidentGroup;
	}

	@Transient
	public String getIncidentGroupName() {
		return incidentGroupName;
	}

	public void setIncidentGroupName(String incidentGroupName) {
		this.incidentGroupName = incidentGroupName;
	}

	@Column(name = "owner", columnDefinition = "VARCHAR(256)", nullable = false)
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Transient
	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "resolution_project_id")
	public Project getResolutionProject() {
		return this.resolutionProject;
	}

	public void setResolutionProject(Project resolutionProject) {
		this.resolutionProject = resolutionProject;
	}

	@Access(AccessType.PROPERTY)
	@Transient
	public String getProjectName() {
		return resolutionProjectName;
	}

	public void setProjectName(String projectName) {
		this.resolutionProjectName = projectName;
	}

	@Transient
	public String getResolutionProjectName() {
		return resolutionProjectName;
	}

	public void setResolutionProjectName(String resolutionProjectName) {
		this.resolutionProjectName = resolutionProjectName;
	}

	@Column(name = "sri_artifact", nullable = true)
	public String getSriArtifact() {
		return sriArtifact;
	}

	public void setSriArtifact(String sriArtifact) {
		this.sriArtifact = sriArtifact;
	}

	// @Temporal(TemporalType.DATE)
    @Column(name = "est_completion_date", nullable = false)
    public Date getEstCompletionDate() {
        return estCompletionDate;
    }

	public void setEstCompletionDate(Date estCompletionDate) {
		this.estCompletionDate = estCompletionDate;
	}

	@OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rd_status_id", referencedColumnName = "id", nullable = false)
    public ReferenceData getStatus() {
        return status;
    }

	public void setStatus(ReferenceData status) {
		this.status = status;
	}

    @Transient
    public String getStatusName() {
        return statusName;
    }

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rd_type_id", referencedColumnName = "id")
    public ReferenceData getType() {
        return type;
    }

	public void setType(ReferenceData type) {
		this.type = type;
	}

    @Transient
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

	@Override
	public String toString() {
		return "IncidentResolution{" +
				"id=" + id +
				", description='" + description + '\'' +
				", horizon=" + horizon +
				", actualCompletionDate=" + actualCompletionDate +
				", incidentGroup=" + incidentGroup +
				", owner='" + owner + '\'' +
				", sriArtifact='" + sriArtifact + '\'' +
				", estCompletionDate=" + estCompletionDate +
				", status=" + status +
				", type=" + type +
				'}';
	}

}
