package com.skkil.sync.badge.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.badge.dto.response.GetBadgeCollectionsResponse;
import com.skkil.sync.badge.service.BadgeCollectionQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BadgeCollectionController {

  private final BadgeCollectionQueryService badgeCollectionQueryService;

  public BadgeCollectionController(BadgeCollectionQueryService badgeCollectionQueryService) {
    this.badgeCollectionQueryService = badgeCollectionQueryService;
  }

  @GetMapping("/users/{userId}/badges")
  @ResponseStatus(HttpStatus.OK)
  public GetBadgeCollectionsResponse getBadgeCollections(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long userId) {
    return badgeCollectionQueryService.getBadgeCollections(user, userId);
  }
}
