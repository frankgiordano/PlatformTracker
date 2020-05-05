package us.com.plattrk.api.model;

import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

@Entity
@Table(name = "RCA")  // RCA = Root Cause Analysis
@NamedQueries({
        @NamedQuery(name = RCA.FIND_ALL_RCAS,
                query = "Select new us.com.plattrk.api.model.RCAVO(rc.id, rc.owner, c.displayName, re.displayName, rc.problem, rc.dueDate, rc.completionDate," +
                        " s.displayName, ig.name, rc.whys) from RCA as rc  join rc.incidentGroup ig join rc.status s join rc.category c join rc.resource re order by ig.name ",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = RCA.FIND_ALL_RCAS_BY_CRITERIA,
                query = "Select new us.com.plattrk.api.model.RCAVO(rc.id, rc.owner, c.displayName, re.displayName, rc.problem, rc.dueDate, rc.completionDate," +
                        " s.displayName, ig.name, rc.whys) from RCA as rc join rc.incidentGroup ig join rc.status s join rc.category c join rc.resource re where lower(ig.name) LIKE (:name) order by ig.name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = RCA.FIND_ALL_RCAS_COUNT_BY_CRITERIA,
                query = "Select count(rc.id) from RCA as rc join rc.incidentGroup ig join rc.status s join rc.category c join rc.resource re where lower(ig.name) LIKE (:name) order by ig.name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")}),
        @NamedQuery(name = RCA.FIND_ALL_RCAS_COUNT,
                query = "Select count(rc.id) from RCA as rc",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "false")})
})
public class RCA {

    public static final String FIND_ALL_RCAS = "findRCAs";
    public static final String FIND_ALL_RCAS_BY_CRITERIA = "FindAllRCAsByCriteria";
    public static final String FIND_ALL_RCAS_COUNT_BY_CRITERIA = "FindAllRCAsCountByCriteria";
    public static final String FIND_ALL_RCAS_COUNT = "FindAllRCASsCount";

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
               ReferenceData resource, String problem, Date dueDate,
               Date completionDate, ReferenceData status,
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Temporal(TemporalType.DATE)
    @Column(name = "completion_date", nullable = true)
    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date", nullable = true)
    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
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
