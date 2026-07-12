package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.post.dto.response.GetPostRecommendationsResponse;
import com.skkil.sync.post.model.PostRecommendationType;
import com.skkil.sync.post.service.PostRecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostRecommendationController {

  private final PostRecommendationService postRecommendationService;

  public PostRecommendationController(PostRecommendationService postRecommendationService) {
    this.postRecommendationService = postRecommendationService;
  }

  @GetMapping("/posts/recommendations")
  @ResponseStatus(HttpStatus.OK)
  public GetPostRecommendationsResponse getRecommendations(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(required = false) PostRecommendationType type,
      @Validated CursorPaginationRequest pagination) {
    return postRecommendationService.getRecommendations(user.userId(), type, pagination);
  }
}
