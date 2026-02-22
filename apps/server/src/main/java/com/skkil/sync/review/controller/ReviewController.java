package com.skkil.sync.review.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.review.dto.request.CreateReviewRequest;
import com.skkil.sync.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @PostMapping("/providers/{providerId}/reviews")
  @ResponseStatus(HttpStatus.CREATED)
  public void createReview(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long providerId,
      @RequestBody @Validated CreateReviewRequest request) {
    reviewService.createReview(user.userId(), providerId, request);
  }
}
