package com.skkil.sync.reflection.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public record GetTagsResponse(List<Tag> tags) {

  @Builder
  public record Tag(
      Long id,
      String name,
      String description,
      Long postCount,
      boolean verified,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {}
}
