package com.skkil.sync.reflection.dto.response;

import java.util.List;
import lombok.Builder;

public record SearchTagsResponse(List<Tag> tags) {

  @Builder
  public static record Tag(String name, String description, Long postCount) {}
}
