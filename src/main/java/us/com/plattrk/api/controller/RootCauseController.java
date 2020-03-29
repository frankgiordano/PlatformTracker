package us.com.plattrk.api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;
import us.com.plattrk.service.RCAService;

@RestController
@RequestMapping(value = "/rootcause")
public class RootCauseController {

    @Autowired
    private RCAService rootCauseService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public RCA deleteRootCause(@PathVariable Long id) {
        return rootCauseService.deleteRCA(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = "application/json")
    public RCA getRootCause(@PathVariable Long id) {
        return rootCauseService.getRCA(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    public List<RCAVO> getRootCauses() {
        return rootCauseService.getRCAs();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public RCA saveRootCause(@RequestBody RCA rca) {
        return rootCauseService.saveRCA(rca);
    }

}
