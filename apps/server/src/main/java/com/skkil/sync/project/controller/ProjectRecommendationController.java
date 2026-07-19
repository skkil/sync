package com.skkil.sync.project.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.project.dto.response.GetProjectRecommendationsResponse;
import com.skkil.sync.project.model.ProjectRecommendationType;
import com.skkil.sync.project.service.ProjectRecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectRecommendationController {

  private final ProjectRecommendationService projectRecommendationService;

  public ProjectRecommendationController(
      ProjectRecommendationService projectRecommendationService) {
    this.projectRecommendationService = projectRecommendationService;
  }

  @GetMapping("/projects/recommendations")
  @ResponseStatus(HttpStatus.OK)
  public GetProjectRecommendationsResponse getProjectRecommendations(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(required = false) ProjectRecommendationType type) {
    return projectRecommendationService.getRecommendations(user.userId(), type);
  }
}
