package us.com.plattrk.api.model;

import java.util.Date;

public class RCAVO {

    private Long id;
    private String category;
    private Date completionDate;
    private Date dueDate;
    private String incidentGroup;
    private String owner;
    private String problem;
    private String resource;
    private String status;
    private String whys;

    public RCAVO() {
    }

    public RCAVO(Long id, String owner, String category, String resource, String problem, Date dueDate,
            Date completionDate, String status, String incidentGroup, String whys) {
        this.id = id;
        this.owner = owner;
        this.category = category;
        this.resource = resource;
        this.problem = problem;
        this.dueDate = dueDate;
        this.completionDate = completionDate;
        this.status = status;
        this.incidentGroup = incidentGroup;
        this.whys = whys;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getIncidentGroup() {
        return incidentGroup;
    }

    public void setIncidentGroup(String incidentGroup) {
        this.incidentGroup = incidentGroup;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWhys() {
        return whys;
    }

    public void setWhys(String whys) {
        this.whys = whys;
    }

}