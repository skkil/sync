package com.skkil.sync.post.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.OffsetPaginationResponse;
import com.skkil.sync.post.model.PostReportReason;
import com.skkil.sync.post.model.PostReportStatus;
import com.skkil.sync.post.model.PostVisibility;
import com.skkil.sync.user.dto.summary.UserSummary;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record GetPostReportsResponse(OffsetPaginationResponse<Report> reports) {

  public record Report(
      Long id,
      Post post,
      UserSummary reporter,
      PostReportReason reason,
      String description,
      PostReportStatus status,
      Instant createdAt,
      @Nullable UserSummary reviewedBy,
      Instant reviewedAt,
      String resolutionNote) {}

  public record Post(
      Long id, String slug, String title, String content, PostVisibility visibility) {}
}
