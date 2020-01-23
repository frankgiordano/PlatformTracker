package us.com.plattrk.service;

import java.util.List;
import java.util.Set;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.IncidentResolutionVO;
import us.com.plattrk.api.model.Status;

public interface IncidentResolutionService {
	Set<IncidentResolution> getIncidentResolutions();
	
	boolean deleteResolution(Long id);
	
	boolean saveResolution(IncidentResolution incidentResolution);
	
	Set<Status> getStatusList();
	
	boolean saveLinkedResolutions(List <IncidentResolutionVO> resolutions);
	
	IncidentResolution getIncidentResolution(Long id);

	Set<IncidentResolution> getGroupResolutions(Long id);


}