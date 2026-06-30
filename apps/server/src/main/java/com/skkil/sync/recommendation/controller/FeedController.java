package com.skkil.sync.recommendation.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.recommendation.dto.response.GetFeedResponse;
import com.skkil.sync.recommendation.service.FeedService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedController {

  private final FeedService feedService;

  public FeedController(FeedService feedService) {
    this.feedService = feedService;
  }

  @GetMapping("/feed/recent")
  @ResponseStatus(HttpStatus.OK)
  public GetFeedResponse getRecentFeed(
      @AuthenticationPrincipal AuthenticatedUser user,
      @Validated CursorPaginationRequest pagination) {
    return feedService.getRecentFeed(user == null ? null : user.userId(), pagination);
  }
}
