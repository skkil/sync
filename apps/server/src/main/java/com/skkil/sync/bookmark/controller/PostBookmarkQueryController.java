package com.skkil.sync.bookmark.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.bookmark.dto.response.GetBookmarkedPostsResponse;
import com.skkil.sync.bookmark.service.PostBookmarkQueryService;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostBookmarkQueryController {

  private final PostBookmarkQueryService postBookmarkQueryService;

  public PostBookmarkQueryController(PostBookmarkQueryService postBookmarkQueryService) {
    this.postBookmarkQueryService = postBookmarkQueryService;
  }

  @GetMapping("/bookmarks/posts")
  @ResponseStatus(HttpStatus.OK)
  public GetBookmarkedPostsResponse getBookmarkedPosts(
      @AuthenticationPrincipal AuthenticatedUser user,
      @Validated CursorPaginationRequest pagination) {
    return postBookmarkQueryService.getBookmarkedPosts(user.userId(), pagination);
  }
}
