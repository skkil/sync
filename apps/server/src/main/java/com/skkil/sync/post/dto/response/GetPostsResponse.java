package com.skkil.sync.post.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.post.model.PostType;
import java.time.LocalDateTime;
import lombok.Builder;

public record GetPostsResponse(CursorPaginationResponse<Post> posts) {

  @Builder
  public static record Post(
      Long id,
      PostType type,
      Author author,
      Project project,
      String content,
      LocalDateTime createdAt) {}

  public static record Author(Long id, String name) {}

  public static record Project(Long id, String name) {}
}
