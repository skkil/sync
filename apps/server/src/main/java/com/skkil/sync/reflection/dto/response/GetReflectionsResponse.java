package com.skkil.sync.reflection.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import java.time.LocalDateTime;
import lombok.Builder;

public record GetReflectionsResponse(CursorPaginationResponse<Reflection> reflections) {

  @Builder
  public static record Reflection(
      Long id, Author author, Project project, String content, LocalDateTime createdAt) {}

  public static record Author(Long id, String name) {}

  public static record Project(Long id, String name) {}
}
