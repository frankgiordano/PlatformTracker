package us.com.plattrk.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.com.plattrk.api.model.OwnerInfo;
import us.com.plattrk.service.OwnerService;

@RestController
@RequestMapping(value = "/ownerService")
public class OwnerController {

	@Autowired
	private OwnerService ownerService;
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/ownersInfo", method = RequestMethod.GET, produces = "application/json")
	public List<OwnerInfo> getOwnerList() throws Exception {
		return ownerService.getOwnerList();
	}	
    
}
