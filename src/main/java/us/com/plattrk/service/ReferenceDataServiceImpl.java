package us.com.plattrk.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import us.com.plattrk.api.model.ReferenceData;
import us.com.plattrk.repository.ReferenceDataRepository;

@Service(value = "ReferenceDataService")
@Scope("singleton")
public class ReferenceDataServiceImpl implements ReferenceDataService {

	@Autowired
	ReferenceDataRepository refRepository;

	private static final ConcurrentMap<Long, List<ReferenceData>> referenceData = new ConcurrentHashMap();

	@Override
	public List<ReferenceData> getReferencesByGroupId(Long groupId) {
		List<ReferenceData> result = referenceData.get(groupId);
		if (result == null || result.size() == 0) {
			result = refRepository.getReferenceDatasByGroupId(groupId);
			if (result != null && result.size() == 0)
				referenceData.put(groupId, result);
		}
		return result;
	}

}
