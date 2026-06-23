package com.skkil.sync.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetPostResponse(
    Long id,
    String slug,
    Author author,
    @Nullable Project project,
    String content,
    Long likeCount,
    Long commentCount,
    boolean bookmarked) {

  @Builder
  public static record Author(Long id, String name) {}

  @Builder
  public static record Project(Long id, String name) {}
}
