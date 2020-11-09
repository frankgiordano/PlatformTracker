package us.com.plattrk.api.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.service.IncidentGroupService;
import us.com.plattrk.api.model.PageWrapper;

@RestController
@RequestMapping(value = "/group")
public class IncidentGroupController {
    
    @Autowired 
    private IncidentGroupService incidentGroupService;
    
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/groups/retrieve", method = RequestMethod.GET, produces = "application/json")
    public Set<IncidentGroup> getGroups() {
        return incidentGroupService.getGroups();
    }
    
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/incidents/retrieve/{id}", method = RequestMethod.GET, produces = "application/json")
    public Set<Incident> getGroupIncidents(@PathVariable Long id) {
        return incidentGroupService.getGroupIncidents(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/retrieve/{id}", method = RequestMethod.GET, produces = "application/json")
    public IncidentGroup getGroup(@PathVariable Long id) {
        return incidentGroupService.getGroup(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/groups/retrieve/{name}/{desc}/{pageIndex}", method = RequestMethod.GET, produces = "application/json")
    PageWrapper<IncidentGroup> search(@PathVariable Map<String, String> filtersMap) {
        return incidentGroupService.search(filtersMap);
    }
    
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public IncidentGroup deleteGroup(@PathVariable Long id) {
        return incidentGroupService.deleteGroup(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public IncidentGroup saveGroup(@RequestBody IncidentGroup group) {
        return incidentGroupService.saveGroup(group);
    }
    
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/deleteallorphans", method = RequestMethod.DELETE, produces = "application/json")
    public List<IncidentGroup> deleteAllOrphanGroups() {
        return incidentGroupService.deleteAllOrphanGroups();
    }

}
