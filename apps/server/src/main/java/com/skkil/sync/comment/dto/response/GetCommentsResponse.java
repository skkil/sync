package com.skkil.sync.comment.dto.response;

import java.time.Instant;
import java.util.List;

public record GetCommentsResponse(List<Comment> comments) {

  public record Comment(
      Long id,
      Author author,
      String content,
      boolean deleted,
      Long likeCount,
      Instant createdAt,
      Instant updatedAt,
      List<Comment> replies) {}

  public record Author(Long id, String handle) {}
}
