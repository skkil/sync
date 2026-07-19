package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.post.dto.response.GetTagRecommendationsResponse;
import com.skkil.sync.post.model.TagRecommendationType;
import com.skkil.sync.post.service.TagRecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TagRecommendationController {

  private final TagRecommendationService tagRecommendationService;

  public TagRecommendationController(TagRecommendationService tagRecommendationService) {
    this.tagRecommendationService = tagRecommendationService;
  }

  @GetMapping("/tags/recommendations")
  @ResponseStatus(HttpStatus.OK)
  public GetTagRecommendationsResponse getTagRecommendations(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(required = false) TagRecommendationType type) {
    return tagRecommendationService.getRecommendations(user.userId(), type);
  }
}
