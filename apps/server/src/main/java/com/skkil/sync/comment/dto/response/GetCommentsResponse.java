package com.skkil.sync.comment.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import java.time.Instant;
import java.util.List;

public record GetCommentsResponse(CursorPaginationResponse<Comment> comments) {

  public record Comment(
      Long id,
      Author author,
      String content,
      boolean deleted,
      Instant createdAt,
      Instant updatedAt,
      List<Comment> replies) {}

  public record Author(Long id, String handle) {}
}
