package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.post.dto.data.PostRecommendationContext;
import com.skkil.sync.post.dto.response.PaginatedGetPostsResponse;
import com.skkil.sync.post.model.PostRecommendationType;
import com.skkil.sync.post.model.PostScope;
import com.skkil.sync.post.model.PostType;
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
  public PaginatedGetPostsResponse getRecommendations(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(required = false) PostRecommendationType type,
      @RequestParam(required = false) PostScope scope,
      @RequestParam(required = false) PostType postType,
      @Validated CursorPaginationRequest pagination) {
    var context = new PostRecommendationContext(user.userId(), scope, postType);
    return postRecommendationService.getRecommendations(context, type, pagination);
  }
}
