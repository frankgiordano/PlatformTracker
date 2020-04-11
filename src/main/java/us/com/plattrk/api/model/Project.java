package us.com.plattrk.api.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import us.com.plattrk.search.Searchable;

@Entity
@Table(name = "Resolution_Project")
@Cacheable(true)
@NamedQueries({@NamedQuery(name = Project.FIND_ALL_PROJECTS, query = "Select new us.com.plattrk.api.model.Project(i.id, i.name, i.owners, i.description, i.eceId,"
        + " i.status, i.estEffort, i.actualEffort, i.actualCompletionDate, i.estcompletionDate, i.pdlcStatus, "
        + " i.recordingDate, i.statusChangeDate, i.wikiType, i.jiraId, i.conflenceId "
        + ") from Project as i order by i.name", hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")})
})
public class Project implements Searchable {

    public static final String FIND_ALL_PROJECTS = "findProjects";

    private Long id;
    private String name;
    private String owners;
    private int isHighPriority;
    private String description;
    private Long eceId;
    private ReferenceData status;
    private Long estEffort;
    private Long actualEffort;
    private Date actualCompletionDate;
    private Set<IncidentResolution> resolutions;
    private Date estcompletionDate;
    private ReferenceData pdlcStatus;
    private Date recordingDate;
    private Date statusChangeDate;
    private ReferenceData wikiType;
    private String jiraId;
    private int conflenceId;

    public Project() {
    }

    public Project(Long id, String name, String owners, String description) {
        this.id = id;
        this.owners = owners;
        this.name = name;
        this.description = description;
    }

    public Project(Long id, String name, String owners, String description,
                   Long eceId, ReferenceData status, Long estEffort,
                   Long actualEffort, Date actualCompletionDate,
                   Date estcompletionDate, ReferenceData pdlcStatus,
                   Date recordingDate, Date statusChangeDate, ReferenceData wikiType,
                   String jiraId, int conflenceId) {
        this.id = id;
        this.owners = owners;
        this.name = name;
        this.description = description;
        this.eceId = eceId;
        this.status = status;
        this.estEffort = estEffort;
        this.actualEffort = actualEffort;
        if (actualCompletionDate != null)
            this.actualCompletionDate = new Date(actualCompletionDate.getTime());
        if (estcompletionDate != null)
            this.estcompletionDate = new Date(estcompletionDate.getTime());
        if (recordingDate != null)
            this.recordingDate = new Date(recordingDate.getTime());
        if (statusChangeDate != null)
            this.statusChangeDate = new Date(statusChangeDate.getTime());
        this.pdlcStatus = pdlcStatus;
        this.wikiType = wikiType;
        this.jiraId = jiraId;
        this.conflenceId = conflenceId;
    }

    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", columnDefinition = "VARCHAR(128)", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "owners", columnDefinition = "VARCHAR(256)", nullable = false)
    public String getOwners() {
        return owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    @Column(name = "is_high_priority", columnDefinition = "DECIMAL(1,0)", nullable = false)
    public int isHighPriority() {
        return isHighPriority;
    }

    public void setHighPriority(int isHighPriority) {
        this.isHighPriority = isHighPriority;
    }

    @Column(name = "description", columnDefinition = "VARCHAR(2000)", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEceId() {
        return eceId;
    }

    public void setEceId(Long eceId) {
        this.eceId = eceId;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rd_status_id", referencedColumnName = "id")
    public ReferenceData getStatus() {
        return status;
    }

    public void setStatus(ReferenceData status) {
        this.status = status;
    }

    public Long getEstEffort() {
        return estEffort;
    }

    public void setEstEffort(Long estEffort) {
        this.estEffort = estEffort;
    }

    public Long getActualEffort() {
        return actualEffort;
    }

    public void setActualEffort(Long actualEffort) {
        this.actualEffort = actualEffort;
    }

    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    public Date getActualCompletionDate() {
        return actualCompletionDate;
    }

    public void setActualCompletionDate(Date actualCompletionDate) {
        this.actualCompletionDate = actualCompletionDate;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToMany(mappedBy = "resolutionProject", fetch = FetchType.EAGER)
    public Set<IncidentResolution> getResolutions() {
        return resolutions;
    }

    public void setResolutions(Set<IncidentResolution> resolutions) {
        this.resolutions = resolutions;
    }

    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    @Column(name = "ESTCOMPLETIONDATE", nullable = false)
    public Date getEstcompletionDate() {
        return estcompletionDate;
    }

    public void setEstcompletionDate(Date estcompletionDate) {
        this.estcompletionDate = estcompletionDate;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rd_pdlc_status_id", referencedColumnName = "id")
    public ReferenceData getPdlcStatus() {
        return pdlcStatus;
    }

    public void setPdlcStatus(ReferenceData pdlcStatus) {
        this.pdlcStatus = pdlcStatus;
    }

    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    @Column(name = "RECORDINGDATE", nullable = false)
    public Date getRecordingDate() {
        return recordingDate;
    }

    public void setRecordingDate(Date recordingDate) {
        this.recordingDate = recordingDate;
    }

    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    public Date getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(Date statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rd_wiki_type_id", referencedColumnName = "id")
    public ReferenceData getWikiType() {
        return wikiType;
    }

    public void setWikiType(ReferenceData wikiType) {
        this.wikiType = wikiType;
    }

    public String getJiraId() {
        return jiraId;
    }

    public void setJiraId(String jiraId) {
        this.jiraId = jiraId;
    }

    public int getConflenceId() {
        return conflenceId;
    }

    public void setConflenceId(int conflenceId) {
        this.conflenceId = conflenceId;
    }

    public static String getFindAllProjects() {
        return FIND_ALL_PROJECTS;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owners='" + owners + '\'' +
                ", isHighPriority=" + isHighPriority +
                ", description='" + description + '\'' +
                ", eceId=" + eceId +
                ", status=" + status +
                ", estEffort=" + estEffort +
                ", actualEffort=" + actualEffort +
                ", actualCompletionDate=" + actualCompletionDate +
                ", estcompletionDate=" + estcompletionDate +
                ", pdlcStatus=" + pdlcStatus +
                ", recordingDate=" + recordingDate +
                ", statusChangeDate=" + statusChangeDate +
                ", wikiType=" + wikiType +
                ", jiraId='" + jiraId + '\'' +
                ", conflenceId=" + conflenceId +
                '}';
    }
    
}
