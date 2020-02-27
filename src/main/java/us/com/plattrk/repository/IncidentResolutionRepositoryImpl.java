package us.com.plattrk.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public boolean saveResolutions(List<IncidentResolution> resolutions) {
        boolean succeed = true;
        try {
            for (int i = 0; i < resolutions.size(); i++) {
                IncidentResolution resolution = resolutions.get(i);
                em.merge(resolution);
            }
            /*
             * int batchSize = 1000; for (int i = 0; i < resolutions.size();
             * i++) { IncidentResolution resolution = resolutions.get(i);
             * em.persist(resolution); if (i % batchSize == 0) { em.flush();
             * em.clear(); } resolutions.add(resolution); } em.flush();
             * em.clear();
             */
        } catch (Exception e) {
            succeed = false;
            e.printStackTrace();
        }
        return succeed;
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
