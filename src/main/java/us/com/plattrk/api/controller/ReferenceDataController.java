package us.com.plattrk.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.com.plattrk.api.model.ReferenceData;
import us.com.plattrk.service.ReferenceDataService;

import java.util.List;

@RestController
@RequestMapping(value = "/reference")
public class ReferenceDataController {

    @Autowired
    private ReferenceDataService refService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/groupData/{groupId}", method = RequestMethod.GET, produces = "application/json")
    public List<ReferenceData> getReferencesByGroupId(@PathVariable Long groupId) {
        return refService.getReferencesByGroupId(groupId);
    }

}
