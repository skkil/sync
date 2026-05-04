package com.skkil.sync.reflection.dto.response;

import lombok.Builder;

@Builder
public record GetReflectionResponse(
    Long id, Author author, String content, Long likeCount, Long commentCount) {

  @Builder
  public static record Author(Long id, String name) {}
}
