package us.com.plattrk.service;

import java.util.List;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.IncidentResolutionVO;
import us.com.plattrk.api.model.Status;

public interface IncidentResolutionService {
	
	List<IncidentResolution> getIncidentResolutions();
	
	boolean deleteResolution(Long id);
	
	boolean saveResolution(IncidentResolution incidentResolution);
	
	List<Status> getStatusList();
	
	boolean saveLinkedResolutions(List <IncidentResolutionVO> resolutions);
	
	IncidentResolution getIncidentResolution(Long id);

	List<IncidentResolution> getGroupResolutions(Long id);

}