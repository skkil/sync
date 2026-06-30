package com.skkil.sync.project.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.project.constants.ProjectConstants;
import com.skkil.sync.project.dto.request.AddTeammateRequest;
import com.skkil.sync.project.dto.request.CreateProjectRequest;
import com.skkil.sync.project.dto.request.UpdateProjectRequest;
import com.skkil.sync.project.dto.request.UpdateTeammateRequest;
import com.skkil.sync.project.dto.response.CreateProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectHandleAvailabilityResponse;
import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectTeammatesResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.dto.response.SearchProjectsResponse;
import com.skkil.sync.project.service.ProjectService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/projects/{handle}")
  @ResponseStatus(HttpStatus.OK)
  public GetProjectResponse getProjectByHandle(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable String handle) {
    return projectService.getProjectByHandle(user != null ? user.userId() : null, handle);
  }

  @GetMapping("/projects/{handle}/teammates")
  public GetProjectTeammatesResponse getProjectTeammates(@PathVariable String handle) {
    return projectService.getProjectTeammates(handle);
  }

  @GetMapping("/users/{handle}/projects")
  public GetProjectsResponse getProjectsByUser(@PathVariable String handle) {
    return projectService.getProjectsByUser(handle);
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

  @PostMapping("/projects/{handle}/teammates")
  @ResponseStatus(HttpStatus.CREATED)
  public void addTeammate(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user,
      @PathVariable String handle,
      @RequestBody @Validated AddTeammateRequest request) {
    projectService.addTeammate(user.userId(), handle, request);
  }

  @PatchMapping("/projects/{handle}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateProject(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user,
      @PathVariable String handle,
      @RequestBody @Validated UpdateProjectRequest request) {
    projectService.updateProject(user.userId(), handle, request);
  }

  @DeleteMapping("/projects/{handle}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProject(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user, @PathVariable String handle) {
    projectService.deleteProject(user.userId(), handle);
  }

  @DeleteMapping("/projects/{handle}/teammates/{teammateHandle}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeTeammate(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user,
      @PathVariable String handle,
      @PathVariable String teammateHandle) {
    projectService.removeTeammate(user.userId(), handle, teammateHandle);
  }

  @PatchMapping("/projects/{handle}/teammates/{teammateHandle}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateTeammateRole(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user,
      @PathVariable String handle,
      @PathVariable String teammateHandle,
      @RequestBody @Validated UpdateTeammateRequest request) {
    projectService.updateTeammateRole(user.userId(), handle, teammateHandle, request);
  }
}
