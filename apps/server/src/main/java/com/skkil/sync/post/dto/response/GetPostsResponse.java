package com.skkil.sync.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.post.model.PostType;
import java.time.OffsetDateTime;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetPostsResponse(CursorPaginationResponse<Post> posts) {

  @Builder
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static record Post(
      Long id,
      String slug,
      PostType type,
      Author author,
      @Nullable Project project,
      String content,
      OffsetDateTime createdAt) {}

  public static record Author(String name, String handle) {}

  public static record Project(String handle, String name) {}
}
