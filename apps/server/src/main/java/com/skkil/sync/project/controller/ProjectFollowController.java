package com.skkil.sync.project.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.project.dto.response.GetProjectFollowersResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.service.ProjectFollowService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class ProjectFollowController {

  private final ProjectFollowService projectFollowService;

  public ProjectFollowController(ProjectFollowService projectFollowService) {
    this.projectFollowService = projectFollowService;
  }

  @PostMapping("/projects/{handle}/follow")
  @ResponseStatus(HttpStatus.OK)
  public void followProject(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user, @PathVariable String handle) {
    projectFollowService.followProject(user.userId(), handle);
  }

  @DeleteMapping("/projects/{handle}/unfollow")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unfollowProject(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user, @PathVariable String handle) {
    projectFollowService.unfollowProject(user.userId(), handle);
  }

  @GetMapping("/projects/{handle}/followers")
  @ResponseStatus(HttpStatus.OK)
  public GetProjectFollowersResponse getProjectFollowers(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable String handle,
      @Validated CursorPaginationRequest pagination) {
    return projectFollowService.getFollowers(
        user != null ? user.userId() : null, handle, pagination);
  }

  @GetMapping("/users/{handle}/followed-projects")
  @ResponseStatus(HttpStatus.OK)
  public GetProjectsResponse getFollowedProjects(@PathVariable String handle) {
    return projectFollowService.getFollowedProjects(handle);
  }
}
