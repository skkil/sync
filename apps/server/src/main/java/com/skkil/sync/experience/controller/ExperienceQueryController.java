package com.skkil.sync.experience.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.experience.dto.response.GetProjectExperiencesResponse;
import com.skkil.sync.experience.service.ExperienceQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExperienceQueryController {

  private final ExperienceQueryService experienceQueryService;

  public ExperienceQueryController(ExperienceQueryService experienceQueryService) {
    this.experienceQueryService = experienceQueryService;
  }

  @GetMapping("/profiles/{userId}/experiences/projects")
  @ResponseStatus(HttpStatus.OK)
  public GetProjectExperiencesResponse getProjectExperiences(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long userId) {
    Long requesterId = user == null ? null : user.userId();
    return experienceQueryService.getProjectExperiences(requesterId, userId);
  }
}
