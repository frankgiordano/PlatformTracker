package us.com.plattrk.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import us.com.plattrk.api.model.IncidentResolution;
import us.com.plattrk.api.model.IncidentResolutionVO;
import us.com.plattrk.api.model.PageWrapper;
import us.com.plattrk.service.IncidentResolutionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/incidentResolution")
public class IncidentResolutionController {

    @Autowired
    private IncidentResolutionService incidentResolutionService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/resolutions/retrieve", method = RequestMethod.GET, produces = "application/json")
    public List<IncidentResolution> getResolutions() {
        return incidentResolutionService.getIncidentResolutions();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/retrieve/{grpName}/{desc}/{assignee}/{pageIndex}", method = RequestMethod.GET, produces = "application/json")
    PageWrapper<IncidentResolution> search(@PathVariable Map<String, String> filtersMap) {
        return incidentResolutionService.search(filtersMap);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public IncidentResolution deleteResolution(@PathVariable Long id) {
        return incidentResolutionService.deleteResolution(id);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public IncidentResolution saveResolution(@RequestBody IncidentResolution resolution) {
        return incidentResolutionService.saveResolution(resolution);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/resolutions/linkProjects", method = RequestMethod.POST, produces = "application/json")
    public List<IncidentResolutionVO> saveLinkedResolutions(@RequestBody List<IncidentResolutionVO> resolutions) {
        return incidentResolutionService.saveLinkedResolutions(resolutions);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/retrieve/resolutions/{id}", method = RequestMethod.GET, produces = "application/json")
    public List<IncidentResolution> getGroupResolutions(@PathVariable Long id) {
        return incidentResolutionService.getGroupResolutions(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/resolutions/retrieve/{id}", method = RequestMethod.GET, produces = "application/json")
    public IncidentResolution getResolution(@PathVariable Long id) {
        String errorMsg = "Incident Resolution id '" + id + "' does not exist";
        return incidentResolutionService.getIncidentResolution(id)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, errorMsg));
    }

}
