package us.com.plattrk.repository;

import java.util.List;

import us.com.plattrk.api.model.ErrorCondition;

public interface ErrorConditionRepository {

	List<ErrorCondition> getErrorConditions();

}
