package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.dto.response.GetAuthenticatedUserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @GetMapping("/users/me")
  public GetAuthenticatedUserResponse getAuthenticatedUser(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
    return new GetAuthenticatedUserResponse(
        authenticatedUser.userId(), authenticatedUser.fullName(), authenticatedUser.email());
  }
}
