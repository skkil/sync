package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.dto.request.UpdateProfileRequest;
import com.skkil.sync.user.service.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

  private final ProfileService profileService;

  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @PatchMapping("/profiles/me")
  public void updateProfile(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody @Validated UpdateProfileRequest request) {
    profileService.updateProfile(user.userId(), request);
  }
}
