package us.com.plattrk.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;
import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;
import us.com.plattrk.service.RCAService;
import us.com.plattrk.api.model.PageWrapper;

@RestController
@RequestMapping(value = "/rootcause")
public class RCAController {

    @Autowired
    private RCAService rcaService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    public List<RCAVO> getRootCauses() {
        return rcaService.getRCAs();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/retrieve/{grpName}/{desc}/{assignee}/{pageIndex}", method = RequestMethod.GET, produces = "application/json")
    PageWrapper<RCA> search(@PathVariable Map<String, String> filtersMap) {
        return rcaService.search(filtersMap);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public RCA deleteRootCause(@PathVariable Long id) {
        return rcaService.deleteRCA(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public RCA saveRootCause(@RequestBody RCA rca) {
        return rcaService.saveRCA(rca);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = "application/json")
    public RCA getRootCause(@PathVariable Long id) {
        String errorMsg = "Root Cause id '" + id + "' does not exist";
        return rcaService.getRCA(id)
                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, errorMsg));
    }

}
