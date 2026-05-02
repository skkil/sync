package com.skkil.sync.provider.project.controller;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.provider.project.dto.request.CreateTeamBuildingPostRequest;
import com.skkil.sync.provider.project.dto.response.CreateTeamBuildingPostResponse;
import com.skkil.sync.provider.project.dto.response.GetTeamBuildingPostsResponse;
import com.skkil.sync.provider.project.service.TeamBuildingService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamBuildingController {

  private final TeamBuildingService teamBuildingService;

  public TeamBuildingController(TeamBuildingService teamBuildingService) {
    this.teamBuildingService = teamBuildingService;
  }

  @PostMapping("/projects/{projectId}/team-building")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateTeamBuildingPostResponse createTeamBuildingPost(
      @PathVariable Long projectId, @RequestBody @Validated CreateTeamBuildingPostRequest request) {
    return teamBuildingService.createTeamBuildingPost(projectId, request);
  }

  @GetMapping("/projects/{projectId}/team-building")
  @ResponseStatus(HttpStatus.OK)
  public GetTeamBuildingPostsResponse getTeamBuildingPostsByProject(
      @PathVariable Long projectId, @Validated CursorPaginationRequest pagination) {
    return teamBuildingService.getTeamBuildingPostsByProject(projectId, pagination);
  }

  @GetMapping("/team-building")
  @ResponseStatus(HttpStatus.OK)
  public GetTeamBuildingPostsResponse getTeamBuildingPosts(
      @Validated CursorPaginationRequest pagination) {
    return teamBuildingService.getTeamBuildingPosts(pagination);
  }
}
