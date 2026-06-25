package com.skkil.sync.post.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.post.model.PostType;
import java.time.LocalDateTime;
import lombok.Builder;

public record GetPostsResponse(CursorPaginationResponse<Post> posts) {

  @Builder
  public static record Post(
      Long id,
      String slug,
      PostType type,
      Author author,
      Project project,
      String content,
      LocalDateTime createdAt) {}

  public static record Author(String name, String handle) {}

  public static record Project(Long id, String name) {}
}
