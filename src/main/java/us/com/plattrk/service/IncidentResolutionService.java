package us.com.plattrk.service;

import java.util.List;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.IncidentResolutionVO;
import us.com.plattrk.api.model.Status;

public interface IncidentResolutionService {
	
	public List<IncidentResolution> getIncidentResolutions();
	
	public boolean deleteResolution(Long id);
	
	public boolean saveResolution(IncidentResolution incidentResolution);
	
	public List<Status> getStatusList();
	
	public boolean saveLinkedResolutions(List <IncidentResolutionVO> resolutions);
	
	public IncidentResolution getIncidentResolution(Long id);

	public List<IncidentResolution> getGroupResolutions(Long id);

}