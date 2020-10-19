package us.com.plattrk.api.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notification")
@NamedQueries({
        @NamedQuery(name = Notification.FIND_NOTIFICATION_BY_TYPE_AND_TYPE_ID,
                query = "Select n from Notification n where n.type = (:type) and n.typeId = (:typeId)")
})
public class Notification {

    public static final String FIND_NOTIFICATION_BY_TYPE_AND_TYPE_ID = "findNotificationByTypeAndTypeId";

    private Long id;
    private String type;
    private Long typeId;
    private LocalDateTime startDateTime;
    private LocalDateTime lastEarlyAlertDateTime;
    private LocalDateTime lastAlertOffSetDateTime;
    private LocalDateTime lastEscalatedAlertDateTime;
    private int numOfChronologies;

    public Notification(Long typeId, LocalDateTime startDateTime, String type, int numOfChronologies) {
        this.type = type;
        this.typeId = typeId;
        this.startDateTime = startDateTime;
        this.numOfChronologies = numOfChronologies;
    }

    public Notification() {
    }

    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "type", nullable = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "type_id", nullable = false)
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @Column(name = "start_date_time", nullable = false)
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    @Column(name = "last_early_alert_date_time", nullable = true)
    public LocalDateTime getLastEarlyAlertDateTime() {
        return lastEarlyAlertDateTime;
    }

    public void setLastEarlyAlertDateTime(LocalDateTime lastEarlyAlertDateTime) {
        this.lastEarlyAlertDateTime = lastEarlyAlertDateTime;
    }

    @Column(name = "last_alert_offset_date_time", nullable = true)
    public LocalDateTime getLastAlertOffSetDateTime() {
        return lastAlertOffSetDateTime;
    }

    public void setLastAlertOffSetDateTime(LocalDateTime lastAlertOffSetDateTime) {
        this.lastAlertOffSetDateTime = lastAlertOffSetDateTime;
    }

    @Column(name = "last_escalated_alert_date_time", nullable = true)
    public LocalDateTime getLastEscalatedAlertDateTime() {
        return lastEscalatedAlertDateTime;
    }

    public void setLastEscalatedAlertDateTime(LocalDateTime lastEscalatedAlertDateTime) {
        this.lastEscalatedAlertDateTime = lastEscalatedAlertDateTime;
    }

    @Column(name = "num_of_chronologies", nullable = true)
    public int getNumOfChronologies() {
        return numOfChronologies;
    }

    public void setNumOfChronologies(int numOfChronologies) {
        this.numOfChronologies = numOfChronologies;
    }

}
