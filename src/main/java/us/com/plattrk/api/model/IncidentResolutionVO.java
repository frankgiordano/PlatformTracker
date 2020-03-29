package us.com.plattrk.api.model;

public class IncidentResolutionVO {
    public class Operation {
           public static final int INSERT = 1;
           public static final int DELETE = 2;
        }
    private Long id;
    private Long projectId;
    
    private int operation;

    
    public IncidentResolutionVO() {
        this(null);
    }

    public IncidentResolutionVO(Long id) {
        this.id = id;
    }
    
    public IncidentResolutionVO(Long id, Long projectId, int operation ) {
        this.id = id;
        this.projectId = projectId;
        this.operation = operation;
    }	 

    public Long getId() {
        return id;
    }

    public int getOperation() {
        return operation;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

}
