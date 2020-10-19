package us.com.plattrk.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import us.com.plattrk.api.model.Notification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Notification getNotification(String type, Long id) {
        List<Notification> result = new ArrayList<>();
        try {
            TypedQuery<Notification> query = em.createNamedQuery(Notification.FIND_NOTIFICATION_BY_TYPE_AND_TYPE_ID, Notification.class)
                                               .setParameter("type", type)
                                               .setParameter("typeId", id);
            result = query.getResultList();
        } catch (PersistenceException e) {
            LOG.error("NotificationRepositoryImpl::getNotification - error retrieving notification for type {} and id {}", type, id);
        }

        if (!result.isEmpty())
            return result.get(0);
        else
            return null;
    }

    @Override
    public Notification save(Notification n) {
        if (n.getId() != null) {
            em.merge(n);
        } else {
            em.persist(n);
            em.flush();
        }
        return n;
    }

    @Override
    public Notification delete(Long id) {
        Optional<Notification> notification = Optional.of(em.find(Notification.class, id));
        notification.ifPresent(lambdaWrapper(n -> {
            em.remove(n);
            em.flush();
        }));

        return notification.orElse(null);
    }

    private static Consumer<Notification> lambdaWrapper(Consumer<Notification> consumer) {
        return n -> {
            try {
                consumer.accept(n);
            } catch (PersistenceException e) {
                LOG.error("NotificationRepositoryImpl::delete - failure deleting incident id {}, msg {}", n.getId(), e.getMessage());
                throw (e);
            }
        };
    }

}
