package us.com.plattrk.repository;

import us.com.plattrk.api.model.ReferenceData;

import java.util.List;

public interface ReferenceDataRepository {

    public List<ReferenceData> getReferenceDataByGroupId(Long groupId);

    public ReferenceData getReferenceData(Long id);

}
