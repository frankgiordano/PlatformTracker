package us.com.plattrk.api.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Priority_Projects")
public class PriorityProjects {
    
    private Long id;
    private Project project;
    private int confluence;
    
    public PriorityProjects() {
    }
    
    public PriorityProjects(int id) {
        this.setConfluenceLink(id);
    }
    @Id
    @Column(name = "priority_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column(name = "confluence_id", nullable = true)
    public int getConfluenceLink() {
        return confluence;
    }

    public void setConfluenceLink(int confluence) {
        this.confluence = confluence;
    }
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    
}
