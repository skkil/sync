package com.skkil.sync.experience.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public record GetReflectionsResponse(List<Reflection> reflections) {

  @Builder
  public static record Reflection(Long id, String content, LocalDateTime createdAt) {}
}
