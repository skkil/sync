package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.dto.request.UpdateProfileRequest;
import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

  private final ProfileService profileService;

  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @GetMapping("/profiles/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public GetProfileResponse getProfile(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long userId) {
    Long requesterId = user == null ? null : user.userId();
    return profileService.getProfile(requesterId, userId);
  }

  @PatchMapping("/profiles/me")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateProfile(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody @Validated UpdateProfileRequest request) {
    profileService.updateProfile(user.userId(), request);
  }
}
