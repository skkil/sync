package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.validator.ValidUsername;
import com.skkil.sync.user.constant.Handle;
import com.skkil.sync.user.dto.request.UpdateProfileRequest;
import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
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

  @GetMapping("/profiles/me")
  @ResponseStatus(HttpStatus.OK)
  public GetProfileResponse getAuthenticatedUser(@AuthenticationPrincipal AuthenticatedUser user) {
    Assert.notNull(user, "Authenticated user must not be null");
    return profileService.getProfileById(user, user.userId());
  }

  @GetMapping("/profiles/{handle}")
  @ResponseStatus(HttpStatus.OK)
  public GetProfileResponse getProfileByHandle(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable @ValidUsername(min = Handle.MIN_LENGTH, max = Handle.MAX_LENGTH)
          String handle) {
    return profileService.getProfileByHandle(user, handle);
  }

  @PatchMapping("/profiles/me")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateProfile(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody @Validated UpdateProfileRequest request) {
    profileService.updateProfile(user.userId(), request);
  }
}
