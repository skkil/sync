package com.skkil.sync.post.dto.response;

import java.util.List;
import lombok.Builder;

public record GetTagsResponse(List<Tag> tags) {

  @Builder
  public static record Tag(Long id, String name, String description, Long postCount) {}
}
