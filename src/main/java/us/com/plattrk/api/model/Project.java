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
import us.com.plattrk.util.JsonDateMinusTimeDeserializer;

@Entity
@Table(name = "Resolution_Project")
@Cacheable(true)
@NamedQueries({
        @NamedQuery(name = Project.FIND_ALL_PROJECTS,
                query = "Select new us.com.plattrk.api.model.Project(pr.id, pr.name, pr.owner, pr.description, pr.ecdeId, pr.status, pr.estEffort, " +
                        "pr.actualEffort, pr.actualCompletionDate, pr.estCompletionDate, pr.pdlcStatus, pr.recordingDate, pr.statusChangeDate, pr.wikiType, " +
                        "pr.jiraId, pr.confluenceId) from Project as pr order by pr.name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = Project.FIND_ALL_PROJECTS_BY_CRITERIA,
                query = "Select new us.com.plattrk.api.model.Project(pr.id, pr.name, pr.owner, pr.description, pr.ecdeId, pr.status, pr.estEffort, " +
                        "pr.actualEffort, pr.actualCompletionDate, pr.estCompletionDate, pr.pdlcStatus, pr.recordingDate, pr.statusChangeDate, pr.wikiType, " +
                        "pr.jiraId, pr.confluenceId) from Project as pr where lower(pr.name) LIKE (:name) order by pr.name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = Project.FIND_ALL_PROJECTS_COUNT_BY_CRITERIA,
                query = "Select count(pr.id) from Project as pr where lower(pr.name) LIKE (:name)",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = Project.FIND_ALL_PROJECTS_COUNT,
                query = "Select count(pr.id) from Project pr",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")})
})
public class Project {

    public static final String FIND_ALL_PROJECTS = "findProjects";
    public static final String FIND_ALL_PROJECTS_BY_CRITERIA = "findAllProjectsByCriteria";
    public static final String FIND_ALL_PROJECTS_COUNT_BY_CRITERIA = "findAllProjectsCountByCriteria";
    public static final String FIND_ALL_PROJECTS_COUNT = "findAllProjectsCount";

    private Long id;
    private String name;
    private String owner;
    private int isHighPriority;
    private String description;
    private Long ecdeId;
    private ReferenceData status;
    private Long estEffort;
    private Long actualEffort;
    private Date actualCompletionDate;
    private Set<IncidentResolution> resolutions;
    private Date estCompletionDate;
    private ReferenceData pdlcStatus;
    private Date recordingDate;
    private Date statusChangeDate;
    private ReferenceData wikiType;
    private String jiraId;
    private int confluenceId;

    public Project() {
    }

    public Project(Long id, String name, String owner, String description) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
    }

    public Project(Long id, String name, String owner, String description,
                   Long ecdeId, ReferenceData status, Long estEffort,
                   Long actualEffort, Date actualCompletionDate,
                   Date estCompletionDate, ReferenceData pdlcStatus,
                   Date recordingDate, Date statusChangeDate, ReferenceData wikiType,
                   String jiraId, int confluenceId) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.ecdeId = ecdeId;
        this.status = status;
        this.estEffort = estEffort;
        this.actualEffort = actualEffort;
        if (actualCompletionDate != null)
            this.actualCompletionDate = new Date(actualCompletionDate.getTime());
        if (estCompletionDate != null)
            this.estCompletionDate = new Date(estCompletionDate.getTime());
        if (recordingDate != null)
            this.recordingDate = new Date(recordingDate.getTime());
        if (statusChangeDate != null)
            this.statusChangeDate = new Date(statusChangeDate.getTime());
        this.pdlcStatus = pdlcStatus;
        this.wikiType = wikiType;
        this.jiraId = jiraId;
        this.confluenceId = confluenceId;
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

    @Column(name = "name", columnDefinition = "VARCHAR(50)", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "owner", columnDefinition = "VARCHAR(256)", nullable = false)
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public Long getEcdeId() {
        return ecdeId;
    }

    public void setEcdeId(Long ecdeId) {
        this.ecdeId = ecdeId;
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
    public Date getEstCompletionDate() {
        return estCompletionDate;
    }

    public void setEstCompletionDate(Date estCompletionDate) {
        this.estCompletionDate = estCompletionDate;
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

    @Column(name = "jira_id", columnDefinition = "VARCHAR(20)", nullable = false)
    public String getJiraId() {
        return jiraId;
    }

    public void setJiraId(String jiraId) {
        this.jiraId = jiraId;
    }

    public int getConfluenceId() {
        return confluenceId;
    }

    public void setConfluenceId(int confluenceId) {
        this.confluenceId = confluenceId;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", isHighPriority=" + isHighPriority +
                ", description='" + description + '\'' +
                ", ecdeId=" + ecdeId +
                ", status=" + status +
                ", estEffort=" + estEffort +
                ", actualEffort=" + actualEffort +
                ", actualCompletionDate=" + actualCompletionDate +
                ", estCompletionDate=" + estCompletionDate +
                ", pdlcStatus=" + pdlcStatus +
                ", recordingDate=" + recordingDate +
                ", statusChangeDate=" + statusChangeDate +
                ", wikiType=" + wikiType +
                ", jiraId='" + jiraId + '\'' +
                ", confluenceId=" + confluenceId +
                '}';
    }

}
