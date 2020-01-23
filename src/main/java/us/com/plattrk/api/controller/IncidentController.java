package us.com.plattrk.api.controller;

import us.com.plattrk.api.model.ErrorCondition;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.api.model.ReferenceData;
import us.com.plattrk.service.IncidentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/incident")
public class IncidentController {

	@Autowired
	private IncidentService incidentService;
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/incidents/retrieve", method = RequestMethod.GET, produces = "application/json")
	public Set<Incident> getIncidents() {
		return incidentService.getIncidents();
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/retrieve/{id}", method = RequestMethod.GET, produces = "application/json")
	public Incident getIncident(@PathVariable Long id) {
		return incidentService.getIncident(id);
	}
	
	@RequestMapping(value = "/retrieve/openincidents", method = RequestMethod.GET, produces = "application/json")
	public List<Incident> getOpenIncidents() {
		return incidentService.getOpenIncidents();
	}

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public boolean deleteIncident(@PathVariable Long id) {
        return incidentService.deleteIncident(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public boolean saveIncident(@RequestBody Incident incident) {
        return incidentService.saveIncident(incident);
    }
    
    @PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/retrieve/group/{id}", method = RequestMethod.GET, produces = "application/json")
	public IncidentGroup getGroup(@PathVariable Long id) {
		return incidentService.getGroup(id);
	}
    
    @PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/retrieve/groups", method = RequestMethod.GET, produces = "application/json")
	public Set<IncidentGroup> getGroups() {
		return incidentService.getGroups();
	}
    
    @PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/retrieve/products/{id}", method = RequestMethod.GET, produces = "application/json")
	public Set<Product> getProducts(@PathVariable Long id) {
		return incidentService.getProducts(id);
	}
    
    @PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/retrieve/errorcode/{id}", method = RequestMethod.GET, produces = "application/json")
	public ErrorCondition getErrorCode(@PathVariable Long id) {
		return incidentService.getErrorCode(id);
	}
    
    @PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/retrieve/applicationstatus/{id}", method = RequestMethod.GET, produces = "application/json")
	public ReferenceData getApplicationStatus(@PathVariable Long id) {
		return incidentService.getApplicationStatus(id);
	}
    
    @PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/retrieve/chronologies/{id}", method = RequestMethod.GET, produces = "application/json")
	public Set<IncidentChronology> getChronologies(@PathVariable Long id) {
		return incidentService.getChronologies(id);
	}

}
