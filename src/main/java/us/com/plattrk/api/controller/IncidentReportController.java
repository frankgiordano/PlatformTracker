package us.com.plattrk.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.com.plattrk.api.model.EmailAddress;
import us.com.plattrk.api.model.IncidentReportByProduct;
import us.com.plattrk.api.model.ToggleSwitch;
import us.com.plattrk.service.IncidentService;

@RestController
@RequestMapping(value = "/incidentreport")
public class IncidentReportController {
	
	@Autowired
	private IncidentService incidentService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/weeklyreport", method = RequestMethod.POST, produces = "application/json")
    public boolean generateWeeklyIncidentReport(@RequestBody EmailAddress address) {
    	return incidentService.generateWeeklyReport(address);
    }
    
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/byproduct", method = RequestMethod.POST, produces = "application/json")
    public boolean generateIncidentReportByProduct(@RequestBody IncidentReportByProduct report) {
    	return incidentService.generateIncidentReportByProduct(report);
    }
    
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/toggle", method = RequestMethod.POST, produces = "application/json")
    public boolean toggleAutoWeeklyReport(@RequestBody ToggleSwitch action) {
    	return incidentService.toggleAutoWeeklyReport(action);
    }
    
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/isToggle", method = RequestMethod.GET, produces = "application/json")
	public boolean isToggleAutoWeeklyReport() {
		return incidentService.isToggleAutoWeeklyReport();
	}

}
