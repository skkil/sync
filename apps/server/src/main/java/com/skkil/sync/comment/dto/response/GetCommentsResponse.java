package com.skkil.sync.comment.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import java.time.OffsetDateTime;
import lombok.Builder;

public record GetCommentsResponse(CursorPaginationResponse<Comment> comments) {

  @Builder
  public record Comment(
      Long id,
      Author author,
      String content,
      boolean isDeleted,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {}

  @Builder
  public record Author(
      Long id, String handle, String name, String profileImageUrl, boolean isPostAuthor) {}
}
