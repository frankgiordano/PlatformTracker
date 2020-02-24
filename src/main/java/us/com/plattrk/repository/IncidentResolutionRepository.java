package us.com.plattrk.repository;

import java.util.List;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.Status;

public interface IncidentResolutionRepository {

	public List<IncidentResolution> getResolutions();

	public boolean deleteResolution(Long id);

	public boolean saveResolution(IncidentResolution resolution);

	public IncidentResolution getResolution(Long id);

	public List<Status> getStatusList();

	public boolean saveResolutions(List<IncidentResolution> resolutions);

	public List<IncidentResolution> getGroupResolutions(Long id);

}
