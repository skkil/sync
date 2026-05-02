package com.skkil.sync.comment.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.comment.dto.request.CreateCommentRequest;
import com.skkil.sync.comment.dto.request.UpdateCommentRequest;
import com.skkil.sync.comment.dto.response.CreateCommentResponse;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.comment.enums.CommentTargetType;
import com.skkil.sync.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {

  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @GetMapping("/comments")
  @ResponseStatus(HttpStatus.OK)
  public GetCommentsResponse getComments(
      @RequestParam CommentTargetType targetType, @RequestParam Long targetId) {
    return commentService.getComments(targetType, targetId);
  }

  @PostMapping("/comments")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateCommentResponse createComment(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody @Validated CreateCommentRequest request) {
    return commentService.createComment(user.userId(), request);
  }

  @PatchMapping("/comments/{commentId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateComment(
      @PathVariable Long commentId, @RequestBody @Validated UpdateCommentRequest request) {
    commentService.updateComment(commentId, request);
  }

  @DeleteMapping("/comments/{commentId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteComment(@PathVariable Long commentId) {
    commentService.deleteComment(commentId);
  }
}
