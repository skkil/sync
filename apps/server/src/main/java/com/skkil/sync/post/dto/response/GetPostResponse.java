package com.skkil.sync.post.dto.response;

import lombok.Builder;

@Builder
public record GetPostResponse(
    Long id,
    String slug,
    Author author,
    Project project,
    String content,
    Long likeCount,
    Long commentCount,
    boolean bookmarked) {

  @Builder
  public static record Author(Long id, String name) {}

  @Builder
  public static record Project(Long id, String name) {}
}
