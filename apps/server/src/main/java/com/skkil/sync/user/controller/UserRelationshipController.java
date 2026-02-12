package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.service.UserRelationshipService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRelationshipController {

  private final UserRelationshipService userRelationshipService;

  public UserRelationshipController(UserRelationshipService userRelationshipService) {
    this.userRelationshipService = userRelationshipService;
  }

  @PostMapping("/users/follow/{followeeId}")
  public void followUser(
      @AuthenticationPrincipal AuthenticatedUser follower, @PathVariable Long followeeId) {
    userRelationshipService.followUser(follower.userId(), followeeId);
  }

  @DeleteMapping("/users/unfollow/{followeeId}")
  public void unfollowUser(
      @AuthenticationPrincipal AuthenticatedUser follower, @PathVariable Long followeeId) {
    userRelationshipService.unfollowUser(follower.userId(), followeeId);
  }
}
