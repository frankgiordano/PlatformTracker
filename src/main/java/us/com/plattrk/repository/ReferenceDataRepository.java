package us.com.plattrk.repository;

import java.util.List;

import us.com.plattrk.api.model.ReferenceData;

public interface ReferenceDataRepository {

	    List<ReferenceData> getReferenceDatasByGroupId(Long groupId);
	    ReferenceData getReferenceData(Long id);
	
}
