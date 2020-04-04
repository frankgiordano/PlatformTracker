package us.com.plattrk.repository;

import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.Status;
import us.com.plattrk.api.model.IncidentResolution;

@Repository
public class IncidentResolutionRepositoryImpl implements IncidentResolutionRepository {

    private static Logger log = LoggerFactory.getLogger(IncidentResolutionRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<IncidentResolution> getResolutions() {
        List<IncidentResolution> myResult = em.createNamedQuery(IncidentResolution.FIND_ALL_RESOLUTIONS).getResultList();
        return myResult;
    }

    @Override
    public IncidentResolution deleteResolution(Long id) {
        IncidentResolution resolution = null;
        try {
            resolution = em.find(IncidentResolution.class, id);
            em.remove(resolution);
            em.flush();
        } catch (PersistenceException e) {
            log.error("IncidentResolution::deleteResolution - failure deleting resolution id " + id + ", msg = " + e.getMessage());
            throw (e);
        }

        return resolution;
    }

    @Override
    public IncidentResolution saveResolution(IncidentResolution resolution) {
        try {
            em.merge(resolution);
        } catch (PersistenceException e) {
            log.error("IncidentResolution::saveResolution - failure saving resolution = " + resolution.toString() + ", msg = " + e.getMessage());
            throw (e);
        }

        return resolution;
    }

    @Override
    public List<IncidentResolution> saveResolutions(List<IncidentResolution> resolutions) {
        resolutions.forEach(lambdaWrapper(resolution -> {
            em.merge(resolution);
        }, resolutions));

        return resolutions;
    }

    private static Consumer<IncidentResolution> lambdaWrapper(Consumer<IncidentResolution> consumer, List<IncidentResolution> resolutions) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (PersistenceException e) {
                log.error("IncidentResolution::saveResolutions - failure saving resolution = " + i.toString() + ", msg = " + e.getMessage());
                resolutions.remove(i);
            }
        };
    } 

    @Override
    public IncidentResolution getResolution(Long id) {
        IncidentResolution incidentResolution = em.find(IncidentResolution.class, id);
        return incidentResolution;
    }

    @Override
    public List<Status> getStatusList() {
        List<Status> myResult = em.createNamedQuery(IncidentResolution.FIND_ALL_STATUS).getResultList();
        return myResult;
    }

    @Override
    public List<IncidentResolution> getGroupResolutions(Long id) {
        List<IncidentResolution> myResult = em.createNamedQuery(IncidentResolution.FIND_ALL_RESOLUTIONS_PER_GROUP).setParameter("pid", id).getResultList();
        return myResult;
    }

}
