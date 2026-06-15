package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.service.PostQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostQueryController {

  private final PostQueryService postQueryService;

  public PostQueryController(PostQueryService postService) {
    this.postQueryService = postService;
  }

  @GetMapping("/posts")
  @ResponseStatus(HttpStatus.OK)
  public GetPostsResponse getPosts(@Validated CursorPaginationRequest pagination) {
    return postQueryService.getPosts(pagination);
  }

  @GetMapping("/posts/{slug}")
  @ResponseStatus(HttpStatus.OK)
  public GetPostResponse getPostBySlug(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable String slug) {
    return postQueryService.getPostBySlug(user == null ? null : user.userId(), slug);
  }

  @GetMapping("/users/{userId}/posts")
  @ResponseStatus(HttpStatus.OK)
  public GetPostsResponse getUserPosts(
      @PathVariable Long userId, @Validated CursorPaginationRequest pagination) {
    return postQueryService.getUserPosts(userId, pagination);
  }

  @GetMapping("/projects/{handle}/posts")
  @ResponseStatus(HttpStatus.OK)
  public GetPostsResponse getPostsByProject(
      @PathVariable String handle, @Validated CursorPaginationRequest pagination) {
    return postQueryService.getPostsByProject(handle, pagination);
  }
}
