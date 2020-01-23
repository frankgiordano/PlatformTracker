package us.com.plattrk.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.com.plattrk.api.model.ErrorCondition;
import us.com.plattrk.service.ErrorConditionService;

@RestController
@RequestMapping(value = "/condition")
public class ErrorConditionController {
		
		@Autowired
		private ErrorConditionService errorConditionService;
		
		@PreAuthorize("isAuthenticated()")
		@RequestMapping(value = "/errors", method = RequestMethod.GET, produces = "application/json")
		public List<ErrorCondition> getErrorConditions() {
			return errorConditionService.getErrorConditions();
		}

}
