package com.skkil.sync.bookmark.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.bookmark.service.PostBookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostBookmarkController {

  private final PostBookmarkService postBookmarkService;

  public PostBookmarkController(PostBookmarkService postBookmarkService) {
    this.postBookmarkService = postBookmarkService;
  }

  @PostMapping("/posts/{postId}/bookmarks")
  @ResponseStatus(HttpStatus.OK)
  public void bookmarkPost(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long postId) {
    postBookmarkService.bookmarkPost(user.userId(), postId);
  }

  @DeleteMapping("/posts/{postId}/bookmarks")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unbookmarkPost(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long postId) {
    postBookmarkService.unbookmarkPost(user.userId(), postId);
  }
}
