package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.post.dto.response.GetFollowedTagsResponse;
import com.skkil.sync.post.service.TagFollowService;
import jakarta.validation.constraints.NotNull;
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
@Validated
public class TagFollowController {

  private final TagFollowService tagFollowService;

  public TagFollowController(TagFollowService tagFollowService) {
    this.tagFollowService = tagFollowService;
  }

  @PostMapping("/tags/{tagId}/follow")
  @ResponseStatus(HttpStatus.OK)
  public void followTag(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user, @PathVariable Long tagId) {
    tagFollowService.followTag(user.userId(), tagId);
  }

  @DeleteMapping("/tags/{tagId}/unfollow")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unfollowTag(
      @AuthenticationPrincipal @NotNull AuthenticatedUser user, @PathVariable Long tagId) {
    tagFollowService.unfollowTag(user.userId(), tagId);
  }

  @GetMapping("/users/{handle}/followed-tags")
  @ResponseStatus(HttpStatus.OK)
  public GetFollowedTagsResponse getFollowedTags(
      @PathVariable String handle, @Validated OffsetPaginationRequest pagination) {
    return tagFollowService.getFollowedTags(handle, pagination);
  }
}
