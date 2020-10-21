package us.com.plattrk.repository;

import us.com.plattrk.api.model.Notification;

public interface NotificationRepository {

    public Notification getNotification(String type, Long id);

    public Notification save(Notification n);

    public Notification delete(Long id);

}
