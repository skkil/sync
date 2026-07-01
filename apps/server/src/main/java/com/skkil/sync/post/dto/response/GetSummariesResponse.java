package com.skkil.sync.post.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import java.time.OffsetDateTime;
import lombok.Builder;

public record GetSummariesResponse(CursorPaginationResponse<Summary> summaries) {

  @Builder
  public static record Summary(
      Long postId, String slug, String title, String displayText, OffsetDateTime createdAt) {}
}
