package us.com.plattrk.api.model;

import java.util.Date;

public class RCAVO {

    private String category;
    private Date completionDate;
    private Date dueDate;
    private Long id;
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

    public String getCategory() {
        return category;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Long getId() {
        return id;
    }

    public String getIncidentGroup() {
        return incidentGroup;
    }

    public String getOwner() {
        return owner;
    }

    public String getProblem() {
        return problem;
    }

    public String getResource() {
        return resource;
    }

    public String getStatus() {
        return status;
    }

    public String getWhys() {
        return whys;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIncidentGroup(String incidentGroup) {
        this.incidentGroup = incidentGroup;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setWhys(String whys) {
        this.whys = whys;
    }

}