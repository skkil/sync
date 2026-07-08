package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.user.dto.response.GetConnectionsResponse;
import com.skkil.sync.user.service.UserRelationshipService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRelationshipController {

  private final UserRelationshipService userRelationshipService;

  public UserRelationshipController(UserRelationshipService userRelationshipService) {
    this.userRelationshipService = userRelationshipService;
  }

  @PostMapping("/users/follow/{followeeId}")
  @ResponseStatus(HttpStatus.OK)
  public void followUser(
      @AuthenticationPrincipal AuthenticatedUser follower, @PathVariable Long followeeId) {
    userRelationshipService.followUser(follower.userId(), followeeId);
  }

  @DeleteMapping("/users/unfollow/{followeeId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unfollowUser(
      @AuthenticationPrincipal AuthenticatedUser follower, @PathVariable Long followeeId) {
    userRelationshipService.unfollowUser(follower.userId(), followeeId);
  }

  @GetMapping("/users/{userId}/following")
  @ResponseStatus(HttpStatus.OK)
  public GetConnectionsResponse getFollowing(
      @PathVariable Long userId, @Validated CursorPaginationRequest pagination) {
    return userRelationshipService.getFollowing(userId, pagination);
  }

  @GetMapping("/users/{userId}/followers")
  @ResponseStatus(HttpStatus.OK)
  public GetConnectionsResponse getFollowers(
      @PathVariable Long userId, @Validated CursorPaginationRequest pagination) {
    return userRelationshipService.getFollowers(userId, pagination);
  }
}
