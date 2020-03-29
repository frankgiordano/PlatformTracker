package us.com.plattrk.service;

import java.util.List;

import us.com.plattrk.api.model.ReferenceData;

public interface ReferenceDataService {

    public List<ReferenceData> getReferencesByGroupId(Long groupId);

}
