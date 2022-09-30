package us.com.plattrk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.com.plattrk.api.model.Severity;
import us.com.plattrk.repository.SeverityRepository;

import java.util.List;

@Service(value = "SeverityService")
public class SeverityServiceImpl implements SeverityService {

    @Autowired
    public SeverityRepository severityRepository;
    
    @Override
    public List<Severity> getSeverities() {
        return severityRepository.getSeverities();
    }

}
