package us.com.plattrk.service;

import us.com.plattrk.api.model.ReferenceData;

import java.util.List;

public interface ReferenceDataService {

    public List<ReferenceData> getReferencesByGroupId(Long groupId);

}
