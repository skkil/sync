package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.post.dto.request.CreatePostRequest;
import com.skkil.sync.post.dto.request.UpdatePostRequest;
import com.skkil.sync.post.dto.request.UpdatePostSummaryRequest;
import com.skkil.sync.post.dto.response.CreatePostResponse;
import com.skkil.sync.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @PostMapping("/posts")
  @ResponseStatus(HttpStatus.CREATED)
  public CreatePostResponse createPost(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody @Validated CreatePostRequest request) {
    return postService.createPost(user.userId(), request);
  }

  @PatchMapping("/posts/{postId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updatePost(
      @PathVariable Long postId, @RequestBody @Validated UpdatePostRequest request) {
    postService.updatePost(postId, request);
  }

  @PatchMapping("/posts/{postId}/summary")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updatePostSummary(
      @PathVariable Long postId, @RequestBody @Validated UpdatePostSummaryRequest request) {
    postService.updatePostSummary(postId, request);
  }

  @PutMapping("/posts/{postId}/likes")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void likePost(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long postId) {
    postService.likePost(user.userId(), postId);
  }

  @DeleteMapping("/posts/{postId}/likes")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unlikePost(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long postId) {
    postService.unlikePost(user.userId(), postId);
  }

  @DeleteMapping("/posts/{postId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePost(@PathVariable Long postId) {
    postService.deletePost(postId);
  }
}
