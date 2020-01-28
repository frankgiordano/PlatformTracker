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
        + ") from Project as i", hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")})
})


//, join fetch i.pdlcStatus, join fetch i.wikiType, join fetch i.status

public class Project implements Searchable {

    public static String getFindAllProjects() {
        return FIND_ALL_PROJECTS;
    }
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
                   Long actualEffort, java.util.Date actualCompletionDate,
                   java.util.Date estcompletionDate, ReferenceData pdlcStatus,
                   java.util.Date recordingDate, java.util.Date statusChangeDate, ReferenceData wikiType,
                   String jiraId, int conflenceId) {
        this.id = id;
        this.owners = owners;
        this.name = name;
        //	this.isHighPriority = isHighPriority;
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

    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    public Date getActualCompletionDate() {
        return actualCompletionDate;
    }

    public Long getActualEffort() {
        return actualEffort;
    }

    public int getConflenceId() {
        return conflenceId;
    }

    @Column(name = "description", columnDefinition = "VARCHAR(2000)", nullable = false)
    public String getDescription() {
        return description;
    }

    public Long getEceId() {
        return eceId;
    }

    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    @Column(name = "ESTCOMPLETIONDATE", nullable = false)
    public Date getEstcompletionDate() {
        return estcompletionDate;
    }

    public Long getEstEffort() {
        return estEffort;
    }

    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public String getJiraId() {
        return jiraId;
    }

    @Column(name = "name", columnDefinition = "VARCHAR(128)", nullable = false)
    public String getName() {
        return name;
    }

    @Column(name = "owners", columnDefinition = "VARCHAR(256)", nullable = false)
    public String getOwners() {
        return owners;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rd_pdlc_status_id", referencedColumnName = "id")
    public ReferenceData getPdlcStatus() {
        return pdlcStatus;
    }

    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    @Column(name = "RECORDINGDATE", nullable = false)
    public Date getRecordingDate() {
        return recordingDate;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToMany(mappedBy = "resolutionProject", fetch = FetchType.EAGER)
    public Set<IncidentResolution> getResolutions() {
        return resolutions;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rd_status_id", referencedColumnName = "id")
    public ReferenceData getStatus() {
        return status;
    }

    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    public Date getStatusChangeDate() {
        return statusChangeDate;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rd_wiki_type_id", referencedColumnName = "id")
    public ReferenceData getWikiType() {
        return wikiType;
    }

    @Column(name = "is_high_priority", columnDefinition = "DECIMAL(1,0)", nullable = false)
    public int isHighPriority() {
        return isHighPriority;
    }

    public void setActualCompletionDate(Date actualCompletionDate) {
        this.actualCompletionDate = actualCompletionDate;
    }

    public void setActualEffort(Long actualEffort) {
        this.actualEffort = actualEffort;
    }

    public void setConflenceId(int conflenceId) {
        this.conflenceId = conflenceId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEceId(Long eceId) {
        this.eceId = eceId;
    }

    public void setEstcompletionDate(Date estcompletionDate) {
        this.estcompletionDate = estcompletionDate;
    }

    public void setEstEffort(Long estEffort) {
        this.estEffort = estEffort;
    }

    public void setHighPriority(int isHighPriority) {
        this.isHighPriority = isHighPriority;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setJiraId(String jiraId) {
        this.jiraId = jiraId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    public void setPdlcStatus(ReferenceData pdlcStatus) {
        this.pdlcStatus = pdlcStatus;
    }

    public void setRecordingDate(Date recordingDate) {
        this.recordingDate = recordingDate;
    }

    public void setResolutions(Set<IncidentResolution> resolutions) {
        this.resolutions = resolutions;
    }

    public void setStatus(ReferenceData status) {
        this.status = status;
    }

    public void setStatusChangeDate(Date statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    public void setWikiType(ReferenceData wikiType) {
        this.wikiType = wikiType;
    }
}
