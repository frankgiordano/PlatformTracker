package us.com.plattrk.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import us.com.plattrk.api.model.*;
import us.com.plattrk.service.IncidentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import us.com.plattrk.api.model.PageWrapper;

import javax.persistence.OptimisticLockException;
import java.util.List;
import java.util.Map;
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
    @RequestMapping(value = "/retrieve/{tag}/{desc}/{assignee}/{pageIndex}", method = RequestMethod.GET, produces = "application/json")
    PageWrapper<Incident> search(@PathVariable Map<String, String> filtersMap) {
        return incidentService.search(filtersMap);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public Incident deleteIncident(@PathVariable Long id) {
        return incidentService.deleteIncident(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public Incident saveIncident(@RequestBody Incident incident) throws OptimisticLockException {
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
    @RequestMapping(value = "/retrieve/chronologies/{id}", method = RequestMethod.GET, produces = "application/json")
    public Set<IncidentChronology> getChronologies(@PathVariable Long id) {
        return incidentService.getChronologies(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/retrieve/products/{id}", method = RequestMethod.GET, produces = "application/json")
    public Set<Product> getProducts(@PathVariable Long id) {
        return incidentService.getProducts(id);
    }

    @RequestMapping(value = "/retrieve/openincidents", method = RequestMethod.GET, produces = "application/json")
    public List<Incident> getOpenIncidents() {
        return incidentService.getOpenIncidents();
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
    @RequestMapping(value = "/retrieve/{id}", method = RequestMethod.GET, produces = "application/json")
    public Incident getIncident(@PathVariable Long id) {
        String errorMsg = "Incident id '" + id + "' does not exist";
        return incidentService.getIncident(id)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, errorMsg));
    }

}
