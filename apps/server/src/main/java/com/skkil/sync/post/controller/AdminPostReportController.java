package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.post.dto.request.ReviewPostReportRequest;
import com.skkil.sync.post.dto.response.GetPostReportsResponse;
import com.skkil.sync.post.model.PostReportStatus;
import com.skkil.sync.post.service.PostReportService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminPostReportController {

  private final PostReportService postReportService;

  public AdminPostReportController(PostReportService postReportService) {
    this.postReportService = postReportService;
  }

  @GetMapping("/admin/post-reports")
  @ResponseStatus(HttpStatus.OK)
  public GetPostReportsResponse getPostReports(
      @RequestParam(required = false) PostReportStatus status,
      @Validated OffsetPaginationRequest pagination) {
    return postReportService.getReports(status, pagination);
  }

  @PatchMapping("/admin/post-reports/{reportId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void reviewPostReport(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long reportId,
      @RequestBody @Validated ReviewPostReportRequest request) {
    postReportService.reviewReport(user.userId(), reportId, request);
  }
}
