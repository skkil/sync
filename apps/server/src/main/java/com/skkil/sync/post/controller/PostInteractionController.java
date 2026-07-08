package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.service.PostInteractionService;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostInteractionController {

  private final PostInteractionService postInteractionService;

  public PostInteractionController(PostInteractionService postInteractionService) {
    this.postInteractionService = postInteractionService;
  }

  @PutMapping("/posts/{postId}/likes")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void likePost(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long postId) {
    postInteractionService.likePost(user.userId(), postId);
  }

  @DeleteMapping("/posts/{postId}/likes")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unlikePost(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long postId) {
    postInteractionService.unlikePost(user.userId(), postId);
  }

  @GetMapping("/posts/likes")
  @ResponseStatus(HttpStatus.OK)
  public GetPostsResponse getLikedPosts(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(required = false) @Nullable String projectHandle,
      @Validated CursorPaginationRequest pagination) {
    return postInteractionService.getLikedPosts(user.userId(), projectHandle, pagination);
  }
}
