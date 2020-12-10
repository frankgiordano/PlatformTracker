package us.com.plattrk.api.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import us.com.plattrk.api.model.IncidentChronology;
import us.com.plattrk.service.IncidentChronologyService;

@RestController
@RequestMapping(value = "/chronology")
public class IncidentChronologyController {

    @Autowired
    private IncidentChronologyService incidentChronologyService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/incidents/retrieve", method = RequestMethod.GET, produces = "application/json")
    public Set<IncidentChronology> getIncidentChronologies() {
        return incidentChronologyService.getChronologies();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/retrieve/{id}", method = RequestMethod.GET, produces = "application/json")
    public IncidentChronology getIncidentChronology(@PathVariable Long id) {
        return incidentChronologyService.getIncidentChronology(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public IncidentChronology deleteIncidentChronology(@PathVariable Long id) {
        return incidentChronologyService.deleteIncidentChronology(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public IncidentChronology saveIncidentChronology(@RequestBody IncidentChronology chronology) throws Exception {
        return incidentChronologyService.saveIncidentChronology(chronology);
    }

}
