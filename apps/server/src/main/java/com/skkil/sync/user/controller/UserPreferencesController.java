package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.dto.request.UpdateUserPreferencesRequest;
import com.skkil.sync.user.dto.response.GetUserPreferencesResponse;
import com.skkil.sync.user.service.UserPreferencesService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserPreferencesController {

  private final UserPreferencesService userPreferencesService;

  public UserPreferencesController(UserPreferencesService userPreferencesService) {
    this.userPreferencesService = userPreferencesService;
  }

  @GetMapping("/preferences/me")
  @ResponseStatus(HttpStatus.OK)
  public GetUserPreferencesResponse getUserPreferences(
      @AuthenticationPrincipal AuthenticatedUser user) {
    return userPreferencesService.getUserPreferences(user.userId());
  }

  @PatchMapping("/preferences/me")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUserPreferences(
      @AuthenticationPrincipal AuthenticatedUser user,
      @Validated @RequestBody UpdateUserPreferencesRequest request) {
    userPreferencesService.updateUserPreferences(user.userId(), request);
  }
}
