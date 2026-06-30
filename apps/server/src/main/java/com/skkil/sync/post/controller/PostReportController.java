package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.post.dto.request.ReportPostRequest;
import com.skkil.sync.post.service.PostReportService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostReportController {

  private final PostReportService postReportService;

  public PostReportController(PostReportService postReportService) {
    this.postReportService = postReportService;
  }

  @PostMapping("/posts/{postId}/reports")
  @ResponseStatus(HttpStatus.CREATED)
  public void reportPost(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long postId,
      @RequestBody @Validated ReportPostRequest request) {
    postReportService.reportPost(user.userId(), postId, request);
  }
}
