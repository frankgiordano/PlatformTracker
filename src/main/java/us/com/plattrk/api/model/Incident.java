package us.com.plattrk.api.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "Incident")
@Cacheable(true)
@NamedQueries({
	@NamedQuery(name = Incident.FIND_ALL_INCIDENTS_RELATIONS, 
				query = "Select i from Incident i", 
				hints={@QueryHint(name="org.hibernate.cacheable", value="true")}),
	@NamedQuery(name = Incident.FIND_ALL_INCIDENTS, 
				query = "Select new us.com.plattrk.api.model.Incident(i.id, i.version, i.tag, i.name, i.reportOwner, i.summary, i.customerImpact, i.severity, i.description, e.name, a.displayName, i.locus, i.startTime, i.endTime, i.usersImpacted, i.transactionIdsImpacted, i.callsReceived, i.alertedBy, i.reviewedBy, i.status, i.correctiveAction, i.relatedActions, i.emailRecipents, i.recordedBy, i.issue) from Incident as i inner join i.error e inner join i.applicationStatus a",
				hints={@QueryHint(name="org.hibernate.cacheable", value="true")}),
	@NamedQuery(name = Incident.FIND_INCIDENT_GROUP, query = "Select g FROM IncidentGroup g where g.name = (:name)"),
	@NamedQuery(name = Incident.FIND_ALL_GROUPS,
				query = "Select new us.com.plattrk.api.model.IncidentGroup(g.id, g.name, g.description, g.status) from IncidentGroup as g",
				hints={@QueryHint(name="org.hibernate.cacheable", value="true")}),
	@NamedQuery(name = Incident.FIND_ALL_OPEN_INCIDENTS,
				query = "Select new us.com.plattrk.api.model.Incident(i.id, i.tag, i.description, i.startTime, i.endTime, i.status, i.emailRecipents) from Incident as i where i.status = (:status)"),
	@NamedQuery(name = Incident.FIND_ALL_OPEN_INCIDENTS_RELATIONS,
				query = "Select i from Incident i where i.status = (:status)"),
	@NamedQuery(name = Incident.FIND_ALL_OPEN_INCIDENTS_BY_RANGE_RELATIONS,
				query = "Select i from Incident i where i.startTime >= (:startDate) and i.startTime < (:endDate)"),
	@NamedQuery(name = Incident.FIND_ALL_OPEN_INCIDENTS_BY_RANGE_AND_PRIORITY_RELATIONS,
				query = "Select i from Incident i where i.startTime >= (:startDate) and i.startTime < (:endDate) and i.severity = (:priority)"),
	@NamedQuery(name = Incident.FIND_ALL_OPEN_INCIDENTS_BY_RANGE_AND_APPLICATIONSTATUS_RELATIONS,
				query = "Select i from Incident i inner join i.applicationStatus a where i.startTime >= (:startDate) and i.startTime < (:endDate) and a.displayName = (:applicationStatus)")
})
public class Incident  {

	public static final String FIND_ALL_INCIDENTS = "findAllIncidents";
	public static final String FIND_INCIDENT_GROUP = "findIncidentGroup";
	public static final String FIND_ALL_INCIDENTS_RELATIONS = "findAllIncidentsRelations";
	public static final String FIND_ALL_GROUPS = "findAllGroups";
	public static final String FIND_ALL_OPEN_INCIDENTS = "findAllOpenIncidents";
	public static final String FIND_ALL_OPEN_INCIDENTS_RELATIONS = "findAllOpenIncidentsRelations";
	public static final String FIND_ALL_OPEN_INCIDENTS_BY_RANGE_RELATIONS = "findAllOpeIncidentsByRangeRelations";
	public static final String FIND_ALL_OPEN_INCIDENTS_BY_RANGE_AND_PRIORITY_RELATIONS = "findAllOpeIncidentsByRangeAndPriorityRelations";
	public static final String FIND_ALL_OPEN_INCIDENTS_BY_RANGE_AND_APPLICATIONSTATUS_RELATIONS = "findAllOpeIncidentsByRangeAndApplicationStatusRelations";
	
	private Long id;
	private Long version;
	private String tag;
	private String name;
	private String reportOwner;
	private String summary;
	private String customerImpact;
//	private Severity severity;
	private String severity;
	private String description;
	private ErrorCondition error;
	private String errorName;
	private ReferenceData applicationStatus;
	private String applicationStatusName;
//	private Locus locus; //CLOUD or External
    private String locus;
	private Date startTime;
	private Date endTime;
	private double usersImpacted;
	private int transactionIdsImpacted;
	private int callsReceived;
//	private AlertedBy alertedBy;
	private String alertedBy;
	private String incidentReport;
	private String reviewedBy;
	private String status;
	private String correctiveAction;
	private String relatedActions;
	private String emailRecipents;
	private String recordedBy;
	private String issue;
	private IncidentGroup incidentGroup;
	private Set<IncidentChronology> chronologies = new HashSet<IncidentChronology>();
	private Set<Product> products = new HashSet<Product>();
	
	public Incident() {
	}
	
	public Incident(Long id, Long version, String tag, String name, String reportOwner,
			String summary, String customerImpact, String severity,
			String description, String errorName, String applicationStatusName, String locus, Date startTime,
			Date endTime, double usersImpacted, int transactionIdsImpacted,
			int callsReceived, String alertedBy, String reviewedBy, String status, 
			String correctiveAction, String relatedActions, String emailRecipents, String recordedBy, String issue) {
		this.id = id;
		this.version = version;
		this.tag = tag;
		this.name = name;
		this.reportOwner = reportOwner;
		this.summary = summary;
		this.customerImpact = customerImpact;
		this.severity = severity;
		this.description = description;
		this.errorName = errorName;
		this.applicationStatusName = applicationStatusName;
		this.locus = locus;
		this.startTime = startTime;
		this.endTime = endTime;
		this.usersImpacted = usersImpacted;
		this.transactionIdsImpacted = transactionIdsImpacted;
		this.callsReceived = callsReceived;
		this.alertedBy = alertedBy;
		this.reviewedBy = reviewedBy;
		this.status = status;
		this.correctiveAction = correctiveAction;
		this.relatedActions = relatedActions;
		this.emailRecipents = emailRecipents;
		this.recordedBy = recordedBy;
		this.issue = issue;
	}

	public Incident(Long id, String tag, String description, Date startTime, Date endTime, String status,
			String emailRecipents) {
		this.id = id;
		this.tag = tag;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.emailRecipents = emailRecipents;
	}

	@Id
	@Column(name = "incident_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version")
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "tag", nullable = false)
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Column(name = "name", columnDefinition="VARCHAR(80)", nullable = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "report_owner", columnDefinition="VARCHAR(80)", nullable = true)
	public String getReportOwner() {
		return reportOwner;
	}

	public void setReportOwner(String reportOwner) {
		this.reportOwner = reportOwner;
	}

	@Column(name = "summary", columnDefinition="VARCHAR(4000)", nullable = true)
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Column(name = "customer_impact", columnDefinition="VARCHAR(1000)", nullable = true)
	public String getCustomerImpact() {
		return customerImpact;
	}

	public void setCustomerImpact(String customerImpact) {
		this.customerImpact = customerImpact;
	}

	@Column(name = "severity",  nullable = false)
	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	@Column(name = "description", columnDefinition="VARCHAR(1000)" , nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "error_id", nullable = true)
	public ErrorCondition getError() {
		return error;
	}

	public void setError(ErrorCondition error) {
		this.error = error;
	}

	@Transient
	public String getErrorName() {
		return errorName;
	}

	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "in_status_id", nullable = true)
	public ReferenceData getApplicationStatus() {
		return applicationStatus;
	}

	public void setApplicationStatus(ReferenceData applicationStatus ) {
		this.applicationStatus = applicationStatus;
	}

	@Transient
	public String getApplicationStatusName() {
		return applicationStatusName;
	}

	public void setApplicationStatusName(String applicationStatusName) {
		this.applicationStatusName = applicationStatusName;
	}

	@Column(name = "locus",  nullable = false)
	public String getLocus() {
		return locus;
	}

	public void setLocus(String locus) {
		this.locus = locus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_time",  nullable = false)
	@JsonDeserialize(using=JsonDateTimeDeserializer.class)
	public Date getStartTime() {
		return startTime;
	}

	@JsonSerialize(using=JsonDateTimeSerializer.class)
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time",  nullable = true)
	@JsonDeserialize(using=JsonDateTimeDeserializer.class)
	public Date getEndTime() {
		return endTime;
	}

	@JsonSerialize(using=JsonDateTimeSerializer.class)
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "users_impacted", nullable = false)
	public double getUsersImpacted() {
		return usersImpacted;
	}

	public void setUsersImpacted(double usersImpacted) {
		this.usersImpacted = usersImpacted;
	}

	@Column(name = "transaction_ids_impacted", nullable = false)
	public int getTransactionIdsImpacted() {
		return transactionIdsImpacted;
	}

	public void setTransactionIdsImpacted(int transactionIdsImpacted) {
		this.transactionIdsImpacted = transactionIdsImpacted;
	}

	@Column(name = "calls_received", nullable = false)
	public int getCallsReceived() {
		return callsReceived;
	}

	public void setCallsReceived(int callsReceived) {
		this.callsReceived = callsReceived;
	}

	@Column(name = "alerted_by",  nullable = false)
	public String getAlertedBy() {
		return alertedBy;
	}

	public void setAlertedBy(String alertedBy) {
		this.alertedBy = alertedBy;
	}

	@Column(name = "incident_report", columnDefinition="VARCHAR(500)", nullable = true)
	public String getIncidentReport() {
		return incidentReport;
	}

	public void setIncidentReport(String incidentReport) {
		this.incidentReport = incidentReport;
	}

	@Column(name = "reviewed_by", columnDefinition="VARCHAR(64)", nullable = true)
	public String getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(String reviewedBy) {
		this.reviewedBy = reviewedBy;
	}

	@Column(name = "status", columnDefinition="VARCHAR(10)", nullable = true)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "corrective_action", columnDefinition="VARCHAR(2048)", nullable = true)
	public String getCorrectiveAction() {
		return correctiveAction;
	}

	public void setCorrectiveAction(String correctiveAction) {
		this.correctiveAction = correctiveAction;
	}

	@Column(name = "related_actions", columnDefinition="VARCHAR(2040)", nullable = true)
	public String getRelatedActions() {
		return relatedActions;
	}

	public void setRelatedActions(String relatedActions) {
		this.relatedActions = relatedActions;
	}

	@Column(name = "email_recipents", columnDefinition="VARCHAR(64)", nullable = true)
	public String getEmailRecipents() {
		return emailRecipents;
	}

	public void setEmailRecipents(String emailRecipents) {
		this.emailRecipents = emailRecipents;
	}

	@Column(name = "recorded_by", columnDefinition="VARCHAR(64)", nullable = true)
	public String getRecordedBy() {
		return recordedBy;
	}

	public void setRecordedBy(String recordedBy) {
		this.recordedBy = recordedBy;
	}

	@Column(name = "issue", columnDefinition="VARCHAR(512)", nullable = true)
	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	@JsonBackReference
	@ManyToOne(cascade = CascadeType.PERSIST) //(cascade = CascadeType.ALL)  this cause a delete to go and delete its parent group also - hence, removed.
	@JoinColumn(name = "group_id")			  // used Persist so that parent group can be update during persist this is the case where in incident
	public IncidentGroup getIncidentGroup() { // create screen specifying a non existing Group will create the group and take the incident description
		return incidentGroup;				  // for group description which is required to be filled.
	}

	public void setIncidentGroup(IncidentGroup incidentGroup) {
		this.incidentGroup = incidentGroup;
	}

	@JsonManagedReference
	@OneToMany(targetEntity = IncidentChronology.class, fetch = FetchType.LAZY, mappedBy = "incident", cascade=CascadeType.ALL)
	public Set<IncidentChronology> getChronologies() {
		return chronologies;
	}

	public void setChronologies(Set<IncidentChronology> chronologies) {
		this.chronologies = chronologies;
	}

	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
	@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@ManyToMany(cascade = CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinTable(name="Incident_Product",
			joinColumns={@JoinColumn(name="incident_id")},
			inverseJoinColumns={@JoinColumn(name="product_id")})
	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "Incident{" +
				"id=" + id +
				", version=" + version +
				", tag='" + tag + '\'' +
				", name='" + name + '\'' +
				", reportOwner='" + reportOwner + '\'' +
				", summary='" + summary + '\'' +
				", customerImpact='" + customerImpact + '\'' +
				", severity='" + severity + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
