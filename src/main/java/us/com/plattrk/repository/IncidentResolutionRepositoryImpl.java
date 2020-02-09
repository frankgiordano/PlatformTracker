package us.com.plattrk.repository;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.Status;
import us.com.plattrk.api.model.IncidentResolution;


@Repository
public class IncidentResolutionRepositoryImpl implements IncidentResolutionRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Set<IncidentResolution> getResolutions() {
        @SuppressWarnings("unchecked")
        List<IncidentResolution> myResult = em.createNamedQuery(
                IncidentResolution.FIND_ALL_RESOLUTIONS).getResultList();
        Set<IncidentResolution> incidentResolutions = new HashSet<IncidentResolution>(myResult);
        return incidentResolutions;
    }

    @Override
    public boolean deleteResolution(Long id) {
        try {
            IncidentResolution incidentResolutions = em.find(IncidentResolution.class, id);
            em.remove(incidentResolutions);
            em.flush();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Transactional
    @Override
    public boolean saveResolution(IncidentResolution resolution) {
        try {
            em.merge(resolution);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Transactional
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
    public Set<Status> getStatusList() {
        @SuppressWarnings("unchecked")
        List<Status> myResult = em.createNamedQuery(IncidentResolution.FIND_ALL_STATUS).getResultList();
        Set<Status> list = new HashSet<Status>(myResult);
        return list;
    }

    @Override
    public Set<IncidentResolution> getGroupResolutions(Long id) {
        @SuppressWarnings("unchecked")
        List<IncidentResolution> myResult = em.createNamedQuery(IncidentResolution.FIND_ALL_RESOLUTIONS_PER_GROUP).setParameter("pid", id).getResultList();
        Set<IncidentResolution> resolutions = new HashSet<IncidentResolution>(myResult);
        return resolutions;
    }

}
