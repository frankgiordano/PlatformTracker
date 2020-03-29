package us.com.plattrk.api.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "Incident_Chronology")
@NamedQueries({
    @NamedQuery(name = IncidentChronology.FIND_ALL_INCIDENT_CHRONOLOGY_RELATIONS, 
                query = "Select g from IncidentChronology g", 
                hints={@QueryHint(name="org.hibernate.cacheable", value="false")}),
    @NamedQuery(name = IncidentChronology.FIND_ALL_INCIDENT_CHRONOLOGY,
                query = "Select new us.com.plattrk.api.model.IncidentChronology(g.id, g.dateTime, g.description, g.recordedBy) from IncidentChronology as g",
                hints={@QueryHint(name="org.hibernate.cacheable", value="false")}),
    @NamedQuery(name = IncidentChronology.FIND_ALL_CHRONOLOGY_PER_INCIDENT,
                query = "Select new us.com.plattrk.api.model.IncidentChronology(g.id, g.dateTime, g.description, g.recordedBy) from IncidentChronology as g JOIN g.incident i where i.id = (:id)",
                hints={@QueryHint(name="org.hibernate.cacheable", value="false")})
})
public class IncidentChronology {
    
    public static final String FIND_ALL_INCIDENT_CHRONOLOGY_RELATIONS = "findAllIncidentChronologyRelations";
    public static final String FIND_ALL_INCIDENT_CHRONOLOGY = "findAllChronology";
    public static final String FIND_ALL_CHRONOLOGY_PER_INCIDENT = "findAllChronologyPerIncident";
    
    private Long id;
    private Date dateTime;
    private String description;
    private String recordedBy;
    private Incident incident;
    
    public IncidentChronology () {
    }
    
    public IncidentChronology(Long id, Date dateTime, String description, String recordedBy) {
        this.id = id;
        this.dateTime = dateTime;
        this.description = description;
        this.recordedBy = recordedBy;
    }

    @Id
    @Column(name = "chronology_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_time",  nullable = false)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)	
    public Date getDateTime() {
        return dateTime;
    }

    @JsonSerialize(using=JsonDateTimeSerializer.class)
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Column(name = "description", columnDefinition="VARCHAR(2048)" , nullable=false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @JsonBackReference
    @ManyToOne(targetEntity=Incident.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "incident_id", nullable=true)
    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }
    
    @Column(name = "recorded_by", columnDefinition="VARCHAR(64)", nullable = true)
    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    @Override
    public String toString() {
        return "IncidentChronology{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", recordedBy='" + recordedBy + '\'' +
                '}';
    }

}
