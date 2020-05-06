package us.com.plattrk.service;

import java.util.List;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.IncidentResolutionVO;
import us.com.plattrk.util.PageWrapper;

public interface IncidentResolutionService {
    
    public List<IncidentResolution> getIncidentResolutions();

    public PageWrapper<IncidentResolution> search(String searchTerm, Long pageIndex);
    
    public IncidentResolution deleteResolution(Long id);
    
    public IncidentResolution saveResolution(IncidentResolution incidentResolution);
    
    public List<IncidentResolutionVO> saveLinkedResolutions(List <IncidentResolutionVO> resolutions);

    public List<IncidentResolution> getGroupResolutions(Long id);

    public IncidentResolution getIncidentResolution(Long id);

}