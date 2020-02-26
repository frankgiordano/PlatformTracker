package us.com.plattrk.api.controller;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.service.IncidentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(value = "/app")
public class OpenController {

	@Autowired
	private IncidentService incidentService;
	
	@RequestMapping(value = "/incidents/retrieve", method = RequestMethod.GET, produces = "application/json")
	public Set<Incident> getIncidents() {
		return incidentService.getIncidents();
	}

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public Incident deleteIncident(@PathVariable Long id) {
        return incidentService.deleteIncident(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public Incident saveIncident(@RequestBody Incident incident) {
        return incidentService.saveIncident(incident);
    }

}
