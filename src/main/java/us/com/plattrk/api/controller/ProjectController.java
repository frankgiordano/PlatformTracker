package us.com.plattrk.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import us.com.plattrk.api.model.PageWrapper;
import us.com.plattrk.api.model.Project;
import us.com.plattrk.service.ProjectService;

import javax.persistence.OptimisticLockException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/retrieve", method = RequestMethod.GET, produces = "application/json")
    public List<Project> getProjects() {
        return projectService.getProjects();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/retrieve/{name}/{assignee}/{pageIndex}", method = RequestMethod.GET, produces = "application/json")
    PageWrapper<Project> search(@PathVariable Map<String, String> filtersMap) {
        return projectService.search(filtersMap);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public Project deleteProject(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public Project saveProject(@RequestBody Project project) throws OptimisticLockException {
        return projectService.saveProject(project);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = "application/json")
    public Project getProject(@PathVariable Long id) {
        String errorMsg = "Project id '" + id + "' does not exist";
        return projectService.getProject(id)
                             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, errorMsg));
    }

}
