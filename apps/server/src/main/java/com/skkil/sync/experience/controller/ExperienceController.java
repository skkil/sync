package com.skkil.sync.experience.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.experience.dto.request.CreateExperienceRequest;
import com.skkil.sync.experience.dto.request.UpdateExperienceRequest;
import com.skkil.sync.experience.dto.response.CreateExperienceResponse;
import com.skkil.sync.experience.dto.response.GetExperiencesResponse;
import com.skkil.sync.experience.service.ExperienceService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExperienceController {

  private final ExperienceService experienceService;

  public ExperienceController(ExperienceService experienceService) {
    this.experienceService = experienceService;
  }

  @PostMapping("/experiences")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateExperienceResponse createExperience(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody @Validated CreateExperienceRequest request) {
    return experienceService.createExperience(user.userId(), request);
  }

  @GetMapping("/profiles/{userId}/experiences")
  @ResponseStatus(HttpStatus.OK)
  public GetExperiencesResponse getExperiences(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long userId) {
    Long requesterId = user == null ? null : user.userId();
    return experienceService.getExperiences(userId, requesterId);
  }

  @PatchMapping("/experiences/{experienceId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateExperience(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long experienceId,
      @RequestBody @Validated UpdateExperienceRequest request) {
    experienceService.updateExperience(user.userId(), experienceId, request);
  }
}
