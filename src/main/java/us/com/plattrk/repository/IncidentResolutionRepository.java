package us.com.plattrk.repository;

import java.util.List;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.Status;

public interface IncidentResolutionRepository {
	
    List<IncidentResolution> getResolutions();

    boolean deleteResolution(Long id);

    boolean saveResolution(IncidentResolution resolution);

	IncidentResolution getResolution(Long id);

	List<Status> getStatusList();
	
	boolean saveResolutions(List<IncidentResolution> resolutions);

	List<IncidentResolution> getGroupResolutions(Long id);

}
