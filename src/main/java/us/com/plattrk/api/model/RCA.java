package us.com.plattrk.api.model;

import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import us.com.plattrk.search.Searchable;

import java.util.Date;

@Entity
@Table(name = "RCA")
@NamedQueries({ @NamedQuery(name = RCA.FIND_ALL_RCAS, query = "Select new us.com.plattrk.api.model.RCAVO( i.id, i.owner, c.displayName, re.displayName, i.problem, i.dueDate, i.completionDate, s.displayName, ig.name, i.whys) from RCA as i join i.incidentGroup ig join i.status s join i.category c join i.resource re order by ig.name ") })
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

    @Temporal(TemporalType.DATE)
    @Column(name = "completion_date", nullable = true)
    @JsonDeserialize(using=JsonDateMinusTimeDeserializer.class)
    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date", nullable = true)
    @JsonDeserialize(using=JsonDateMinusTimeDeserializer.class)
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
