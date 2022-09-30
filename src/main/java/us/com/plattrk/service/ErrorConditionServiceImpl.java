package us.com.plattrk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.com.plattrk.api.model.ErrorCondition;
import us.com.plattrk.repository.ErrorConditionRepository;

import java.util.List;

@Service(value = "ErrorConditionService")
public class ErrorConditionServiceImpl implements ErrorConditionService {
    
    @Autowired
    public ErrorConditionRepository errorConditionRepository;

    @Override
    public List<ErrorCondition> getErrorConditions() {
        return errorConditionRepository.getErrorConditions();
    }

}
