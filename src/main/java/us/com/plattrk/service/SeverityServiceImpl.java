package us.com.plattrk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.com.plattrk.api.model.Severity;
import us.com.plattrk.repository.SeverityRepository;

@Service(value = "SeverityService")
public class SeverityServiceImpl implements SeverityService {

	@Autowired
	SeverityRepository severityRepository;
	
	@Override
	public List<Severity> getSeverities() {
		return severityRepository.getSeverities();
	}

}
