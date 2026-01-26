package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.dto.response.GetAuthenticatedUserResponse;
import com.skkil.sync.user.service.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final ProfileService profileService;

  public UserController(ProfileService userService) {
    this.profileService = userService;
  }

  @GetMapping("/users/me")
  public GetAuthenticatedUserResponse getAuthenticatedUser(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
    return profileService.getAuthenticatedUser(authenticatedUser.userId());
  }
}
