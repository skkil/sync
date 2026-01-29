package com.skkil.sync.user.controller;

import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public GetProfileResponse getProfile(@PathVariable Long userId) {
    return profileService.getProfile(userId);
  }
}
