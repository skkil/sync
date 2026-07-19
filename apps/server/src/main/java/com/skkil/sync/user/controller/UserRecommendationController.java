package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.dto.response.GetUserRecommendationsResponse;
import com.skkil.sync.user.model.UserRecommendationType;
import com.skkil.sync.user.service.UserRecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRecommendationController {

  private final UserRecommendationService userRecommendationService;

  public UserRecommendationController(UserRecommendationService userRecommendationService) {
    this.userRecommendationService = userRecommendationService;
  }

  @GetMapping("/users/recommendations")
  @ResponseStatus(HttpStatus.OK)
  public GetUserRecommendationsResponse getUserRecommendations(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(required = false) UserRecommendationType type) {
    return userRecommendationService.getRecommendations(user.userId(), type);
  }
}
