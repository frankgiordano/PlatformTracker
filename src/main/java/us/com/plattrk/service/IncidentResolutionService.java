package us.com.plattrk.service;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.IncidentResolutionVO;
import us.com.plattrk.api.model.PageWrapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IncidentResolutionService {
    
    public List<IncidentResolution> getIncidentResolutions();

    public PageWrapper<IncidentResolution> search(Map<String, String> filtersMap);
    
    public IncidentResolution deleteResolution(Long id);
    
    public IncidentResolution saveResolution(IncidentResolution incidentResolution);
    
    public List<IncidentResolutionVO> saveLinkedResolutions(List <IncidentResolutionVO> resolutions);

    public List<IncidentResolution> getGroupResolutions(Long id);

    public Optional<IncidentResolution> getIncidentResolution(Long id);

}