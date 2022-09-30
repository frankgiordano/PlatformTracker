package us.com.plattrk.repository;

import us.com.plattrk.api.model.ErrorCondition;

import java.util.List;

public interface ErrorConditionRepository {

    public List<ErrorCondition> getErrorConditions();

}
