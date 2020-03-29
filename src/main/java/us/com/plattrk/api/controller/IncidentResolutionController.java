package us.com.plattrk.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.IncidentResolutionVO;
import us.com.plattrk.service.IncidentResolutionService;

@RestController
@RequestMapping(value = "/incidentResolution")
public class IncidentResolutionController {

    @Autowired
    private IncidentResolutionService incidentResolutionService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public IncidentResolution deleteResolution(@PathVariable Long id) {
        return incidentResolutionService.deleteResolution(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/resolutions/retrieve/{id}", method = RequestMethod.GET, produces = "application/json")
    public IncidentResolution getResolution(@PathVariable Long id) {
        return incidentResolutionService.getIncidentResolution(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/resolutions/retrieve", method = RequestMethod.GET, produces = "application/json")
    public List<IncidentResolution> getResolutions() {
        return incidentResolutionService.getIncidentResolutions();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public IncidentResolution saveResolution(@RequestBody IncidentResolution resolution) {
        return incidentResolutionService.saveResolution(resolution);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/resolutions/linkProjects", method = RequestMethod.POST, produces = "application/json")
    public boolean saveLinkedResolutions(@RequestBody List<IncidentResolutionVO> resolutions) {
        return incidentResolutionService.saveLinkedResolutions(resolutions);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/retrieve/resolutions/{id}", method = RequestMethod.GET, produces = "application/json")
    public List<IncidentResolution> getGroupResolutions(@PathVariable Long id) {
        return incidentResolutionService.getGroupResolutions(id);
    }

}
