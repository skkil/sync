package com.skkil.sync.project.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.project.constants.ProjectConstants;
import com.skkil.sync.project.dto.request.CreateProjectRequest;
import com.skkil.sync.project.dto.response.CreateProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectHandleAvailabilityResponse;
import com.skkil.sync.project.dto.response.SearchProjectsResponse;
import com.skkil.sync.project.service.ProjectService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class ProjectController {

  private final ProjectService projectService;

  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @PostMapping("/projects")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateProjectResponse createProject(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user,
      @RequestBody CreateProjectRequest request) {
    return projectService.createProject(user.userId(), request);
  }

  @GetMapping("/projects/handles/availability")
  @ResponseStatus(HttpStatus.OK)
  public GetProjectHandleAvailabilityResponse getProjectHandleAvailability(
      @RequestParam
          @Size(min = ProjectConstants.MIN_HANDLE_LENGTH, max = ProjectConstants.MAX_HANDLE_LENGTH)
          String handle) {
    return projectService.isProjectHandleAvailable(handle);
  }

  @GetMapping("/search/projects")
  @ResponseStatus(HttpStatus.OK)
  public SearchProjectsResponse searchProjects(@RequestParam(required = true) String query) {
    return projectService.searchProjects(query);
  }

  @GetMapping("/search/projects/my")
  @ResponseStatus(HttpStatus.OK)
  public SearchProjectsResponse searchMyProjects(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user,
      @RequestParam(required = true) String query) {
    return projectService.searchMyProjects(user.userId(), query);
  }
}
