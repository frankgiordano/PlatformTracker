package us.com.plattrk.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Repository
public class IncidentChronologyRepositoryImpl implements IncidentChronologyRepository {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentChronologyRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public IncidentChronology saveIncidentChronology(IncidentChronology chronology) {
        try {
            Incident incident = em.find(Incident.class, chronology.getIncident().getId());
            chronology.setIncident(incident);
            // this is usually a choice of persist or merge.. using persist fails with detach entity error
            // because the associated incident exist when persist is done.. needs to be a merge instead..
            em.merge(chronology);
        } catch (PersistenceException e) {
            LOG.error("IncidentChronologyRepositoryImpl::saveIncidentChronology - failure saving chronology {}, msg {}", chronology, e.getMessage());
            throw (e);
        }

        return chronology;
    }

    @Override
    public IncidentChronology deleteIncidentChronology(Long id) {
        Optional<IncidentChronology> chronology = Optional.of(em.find(IncidentChronology.class, id));
        chronology.ifPresent(lambdaWrapper(c -> {
            // need to remove the reference to the other side of this mapping or else it will delete the incident along with the chronology.
            c.setIncident(null);
            em.remove(c);
            em.flush();
        }));

        return chronology.orElse(null);
    }

    @Override
    public Set<IncidentChronology> getChronologies() {
        @SuppressWarnings("unchecked")
        List<IncidentChronology> myResult = em.createNamedQuery(IncidentChronology.FIND_ALL_INCIDENT_CHRONOLOGY).getResultList();
        return new HashSet<IncidentChronology>(myResult);
    }

    @Override
    public Set<IncidentChronology> getChronologiesPerIncident(Long id) {
        @SuppressWarnings("unchecked")
        List<IncidentChronology> myResult = em.createNamedQuery(IncidentChronology.FIND_ALL_CHRONOLOGY_PER_INCIDENT).setParameter("id", id).getResultList();
        return new HashSet<IncidentChronology>(myResult);
    }

    @Override
    public Incident getIncidentOfNewChronology(Long id) {
        return em.find(Incident.class, id);
    }

    @Override
    public IncidentChronology getIncidentChronology(Long id) {
        return em.find(IncidentChronology.class, id);
    }

    private static Consumer<IncidentChronology> lambdaWrapper(Consumer<IncidentChronology> consumer) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                LOG.error("IncidentChronologyRepositoryImpl::deleteIncidentChronology - failure deleting chronology id {}, msg {}", i.getId(), e.getMessage());
                throw (e);
            }
        };
    }

}
