package com.skkil.sync.comment.dto.response;

import java.time.Instant;
import java.util.List;
import lombok.Builder;

public record GetCommentsResponse(List<Comment> comments) {

  @Builder
  public record Comment(
      Long id,
      Author author,
      String content,
      boolean isDeleted,
      Instant createdAt,
      Instant updatedAt) {}

  @Builder
  public record Author(
      Long id, String handle, String name, String profileImageUrl, boolean isPostAuthor) {}
}
