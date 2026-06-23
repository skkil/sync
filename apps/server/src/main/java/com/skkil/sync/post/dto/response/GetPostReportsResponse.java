package com.skkil.sync.post.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.OffsetPaginationResponse;
import com.skkil.sync.post.model.PostReportReason;
import com.skkil.sync.post.model.PostReportStatus;
import com.skkil.sync.post.model.PostVisibility;
import java.time.Instant;

public record GetPostReportsResponse(OffsetPaginationResponse<Report> reports) {

  public record Report(
      Long id,
      Post post,
      Reporter reporter,
      PostReportReason reason,
      String description,
      PostReportStatus status,
      Instant createdAt,
      Reviewer reviewedBy,
      Instant reviewedAt,
      String resolutionNote) {}

  public record Post(
      Long id, String slug, String title, String content, PostVisibility visibility) {}

  public record Reporter(Long id, String handle, String name) {}

  public record Reviewer(Long id, String handle, String name) {}
}
