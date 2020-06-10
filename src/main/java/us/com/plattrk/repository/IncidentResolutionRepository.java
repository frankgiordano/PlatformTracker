package us.com.plattrk.repository;

import java.util.List;
import java.util.Map;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.util.PageWrapper;

public interface IncidentResolutionRepository {

    public List<IncidentResolution> getResolutions();

    public PageWrapper<IncidentResolution> getResolutionsByCriteria(Map<String, String> filtersMap);

    public IncidentResolution deleteResolution(Long id);

    public IncidentResolution saveResolution(IncidentResolution resolution);

    public List<IncidentResolution> saveResolutions(List<IncidentResolution> resolutions);

    public List<IncidentResolution> getGroupResolutions(Long id);

    public IncidentResolution getResolution(Long id);

}
