package com.skkil.sync.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import java.time.LocalDateTime;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetPostsResponse(CursorPaginationResponse<Post> posts) {

  @Builder
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static record Post(
      Long id,
      String slug,
      Author author,
      @Nullable Project project,
      String content,
      LocalDateTime createdAt) {}

  public static record Author(Long id, String name) {}

  public static record Project(Long id, String name) {}
}
