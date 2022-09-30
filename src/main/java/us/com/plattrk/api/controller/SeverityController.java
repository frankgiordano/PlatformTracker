package us.com.plattrk.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.com.plattrk.api.model.Severity;
import us.com.plattrk.service.SeverityService;

import java.util.List;

@RestController
@RequestMapping(value = "/severity")
public class SeverityController {

    @Autowired
    private SeverityService severityService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/severities/retrieve", method = RequestMethod.GET, produces = "application/json")
    public List<Severity> getSeverities() {
        return severityService.getSeverities();
    }

}
