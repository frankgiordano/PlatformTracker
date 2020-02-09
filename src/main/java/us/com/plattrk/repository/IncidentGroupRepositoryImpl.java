package us.com.plattrk.repository;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;

@Repository
public class IncidentGroupRepositoryImpl implements IncidentGroupRepository {
	
	private static Logger log = LoggerFactory.getLogger(IncidentGroupRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public Set<IncidentGroup> getGroups() {
		@SuppressWarnings("unchecked")
		List<IncidentGroup> myResult = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS).getResultList();
		Set<IncidentGroup> groups = new HashSet<IncidentGroup>(myResult);
		return groups;
	}

	@Override
	public Set<Incident> getGroupIncidents(Long id) {
		IncidentGroup incidentGroup = em.find(IncidentGroup.class, id);
		return incidentGroup.getIncidents();
	}
	
	@Override
	public IncidentGroup getGroup(Long id) {
		IncidentGroup incidentGroup = em.find(IncidentGroup.class, id);
		return incidentGroup;
	}

	@Override
	public boolean deleteGroup(Long id) {
		try {
			IncidentGroup group = em.find(IncidentGroup.class, id);
			em.remove(group);
			em.flush();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		}
		
		// workaround where remove no longer sends a delete to the db to error back on constraint like before.. 
		IncidentGroup groupCheck = em.find(IncidentGroup.class, id);
		if (groupCheck != null) {
			// delete of group did not occur return false
			log.error("return false");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean saveGroup(IncidentGroup group) {
		try {
			if (group.getId() == null) {
				em.persist(group);
				em.flush();	
			}
			else {
				em.merge(group);
			}
		} catch (Exception e) {
			log.error("in expection = " + e.getMessage());
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean deleteAllGroupOrphans() {
		
		IncidentGroup groupLookUP = null;
		boolean found = false;
		
		@SuppressWarnings("unchecked")
		List<IncidentGroup> myResult = em.createNamedQuery(IncidentGroup.FIND_ALL_INCIDENT_GROUPS_RELATIONS).getResultList();

		for (Iterator<IncidentGroup> it = myResult.iterator(); it.hasNext(); ) {
			groupLookUP = it.next();
			// see if there are no associated incidents
			if (groupLookUP.getIncidents().isEmpty()) {
				try {
					// find if there are any associated resolutions and RCAs
					// if so, don't forward this group to be removed. 					
					List resolutionChildIds = em.createQuery("select c.id from IncidentResolution c where c.incidentGroup.id = :pid").setParameter("pid", groupLookUP.getId()).getResultList();
					List rcaChildIds = em.createQuery("select c.id from RCA c where c.incidentGroup.id = :pid").setParameter("pid", groupLookUP.getId()).getResultList();
					
					if (!resolutionChildIds.isEmpty() || !rcaChildIds.isEmpty()) {
						continue;
					}
					
					found = true;
					em.remove(groupLookUP);
					em.flush();				
				} catch (Exception e) {
					log.error("in expection = " + e.getMessage());
				}
			}    
		}
		
		return found;
	}
	
}
