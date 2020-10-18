package us.com.plattrk.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import us.com.plattrk.api.model.Notification;
import us.com.plattrk.api.model.Type;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class NotificationRepository {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationRepository.class);

    @PersistenceContext
    private EntityManager em;

    public Notification getNotification(Type type, Long id) {
        Optional<Notification> result = Optional.empty();
        try {
            TypedQuery<Notification> query = em.createNamedQuery(Notification.FIND_NOTIFICATION_BY_TYPE_AND_TYPE_ID, Notification.class)
                                               .setParameter("type", type)
                                               .setParameter("typeId", id);
            result = Optional.of(query.getSingleResult());
        } catch (PersistenceException e) {
            LOG.error("NotificationRepository::getNotification - error retrieving notification for type {} and id {}", type.name(), id);
        }

        return result.orElse(null);
    }

    public Notification save(Notification n) {
        if (n.getId() != null) {
            em.merge(n);
        } else {
            em.persist(n);
            em.flush();
        }
        return n;
    }

}
