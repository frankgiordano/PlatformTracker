package us.com.plattrk.repository;

import java.util.List;
import java.util.Set;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.Status;

public interface IncidentResolutionRepository {
	
    Set<IncidentResolution> getResolutions();

    boolean deleteResolution(Long id);

    boolean saveResolution(IncidentResolution resolution);

	IncidentResolution getResolution(Long id);

	Set<Status> getStatusList();
	
	boolean saveResolutions(List<IncidentResolution> resolutions);

	Set<IncidentResolution> getGroupResolutions(Long id);

}
