package com.skkil.sync.reflection.dto.response;

import java.util.List;
import lombok.Builder;

public record SearchReflectionsResponse(List<Reflection> reflections) {

  @Builder
  public static record Reflection(Long id, String content) {}
}
