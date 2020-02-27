package us.com.plattrk.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;

@Repository
public class IncidentChronologyRepositoryImpl implements IncidentChronologyRepository {

    private static Logger log = LoggerFactory.getLogger(IncidentChronologyRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public IncidentChronology saveIncidentChronology(IncidentChronology chronology) {
        log.info("IncidentChronologyRepositoryImpl::saveIncidentChronology");

        try {
            Incident incident = em.find(Incident.class, chronology.getIncident().getId());
            chronology.setIncident(incident);
			// this is usually a choice of persist or merge.. using persist fails with detach entity error
			// because the associated incident exist when persist is done.. needs to be a merge instead..
            em.merge(chronology);
        } catch (PersistenceException e) {
            log.error("IncidentChronologyRepositoryImpl::saveIncidentChronology - failure saving chronology " + chronology.toString() + ", msg = " + e.getMessage());
            throw (e);
        }

        return chronology;
    }

    @Override
    public IncidentChronology deleteIncidentChronology(Long id) {
        IncidentChronology chronology = null;
        try {
            chronology = em.find(IncidentChronology.class, id);
            chronology.setIncident(null);    // need to remove the reference to the other side of this mapping or else it will delete the incident along with the chronology...
            em.remove(chronology);
            em.flush();
        } catch (PersistenceException e) {
            log.error("IncidentChronologyRepositoryImpl::deleteIncidentChronology - failure deleting chronology id " + id + ", msg = " + e.getMessage());
            throw (e);
        }

        return chronology;
    }

    @Override
    public Set<IncidentChronology> getChronologies() {
        @SuppressWarnings("unchecked")
        List<IncidentChronology> myResult = em.createNamedQuery(IncidentChronology.FIND_ALL_INCIDENT_CHRONOLOGY).getResultList();
        Set<IncidentChronology> chronologies = new HashSet<IncidentChronology>(myResult);
        return chronologies;
    }

    @Override
    public Set<IncidentChronology> getChronologiesPerIncident(Long id) {
        @SuppressWarnings("unchecked")
        List<IncidentChronology> myResult = em.createNamedQuery(IncidentChronology.FIND_ALL_CHRONOLOGY_PER_INCIDENT).setParameter("id", id).getResultList();
        Set<IncidentChronology> chronologies = new HashSet<IncidentChronology>(myResult);
        return chronologies;
    }

    @Override
    public Incident getIncidentOfChronology(Long id) {
        return em.find(Incident.class, id);
    }

}
