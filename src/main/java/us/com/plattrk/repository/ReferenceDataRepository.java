package us.com.plattrk.repository;

import java.util.List;

import us.com.plattrk.api.model.ReferenceData;

public interface ReferenceDataRepository {

    public List<ReferenceData> getReferenceDataByGroupId(Long groupId);

    public ReferenceData getReferenceData(Long id);

}
