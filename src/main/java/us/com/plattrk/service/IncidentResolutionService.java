package us.com.plattrk.service;

import java.util.List;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.IncidentResolutionVO;
import us.com.plattrk.api.model.Status;

public interface IncidentResolutionService {
    
    public List<IncidentResolution> getIncidentResolutions();
    
    public IncidentResolution deleteResolution(Long id);
    
    public IncidentResolution saveResolution(IncidentResolution incidentResolution);
    
    public List<Status> getStatusList();
    
    public List<IncidentResolution> saveLinkedResolutions(List <IncidentResolutionVO> resolutions);
    
    public IncidentResolution getIncidentResolution(Long id);

    public List<IncidentResolution> getGroupResolutions(Long id);

}