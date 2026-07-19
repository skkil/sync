package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.model.PostScope;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.service.PostQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class PostQueryController {

  private final PostQueryService postQueryService;

  public PostQueryController(PostQueryService postService) {
    this.postQueryService = postService;
  }

  @GetMapping("/posts")
  @ResponseStatus(HttpStatus.OK)
  public GetPostsResponse getPosts(
      @AuthenticationPrincipal AuthenticatedUser user,
      @Validated CursorPaginationRequest pagination) {
    return postQueryService.getPosts(user == null ? null : user.userId(), pagination);
  }

  @GetMapping("/posts/drafts")
  @ResponseStatus(HttpStatus.OK)
  public GetPostsResponse getDrafts(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(required = false) PostType type,
      @RequestParam(required = false) PostScope scope,
      @RequestParam(required = false) String projectHandle,
      @Validated CursorPaginationRequest pagination) {
    return postQueryService.getDrafts(user.userId(), type, scope, projectHandle, pagination);
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
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long userId,
      @RequestParam(required = false) PostType type,
      @Validated CursorPaginationRequest pagination) {
    return postQueryService.getUserPosts(
        user == null ? null : user.userId(), userId, type, pagination);
  }

  @GetMapping("/users/{userId}/posts/commented")
  @ResponseStatus(HttpStatus.OK)
  public GetPostsResponse getCommentedPosts(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long userId,
      @RequestParam(required = false) String projectHandle,
      @Validated CursorPaginationRequest pagination) {
    return postQueryService.getCommentedPosts(
        user == null ? null : user.userId(), userId, projectHandle, pagination);
  }

  @GetMapping("/tags/{tagId}/posts")
  @ResponseStatus(HttpStatus.OK)
  public GetPostsResponse getPostsByTag(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long tagId,
      @RequestParam(required = false) PostType type,
      @Validated CursorPaginationRequest pagination) {
    return postQueryService.getPostsByTag(
        user == null ? null : user.userId(), tagId, type, pagination);
  }

  @GetMapping("/projects/{handle}/posts")
  @ResponseStatus(HttpStatus.OK)
  public GetPostsResponse getPostsByProject(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable String handle,
      @RequestParam(required = false) PostType type,
      @RequestParam(required = false) String authorHandle,
      @Validated CursorPaginationRequest pagination) {
    return postQueryService.getPostsByProject(
        user == null ? null : user.userId(), handle, type, authorHandle, pagination);
  }
}
