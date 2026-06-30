package com.skkil.sync.bookmark.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import java.time.LocalDateTime;
import lombok.Builder;

public record GetBookmarkedPostsResponse(CursorPaginationResponse<Post> posts) {

  @Builder
  public static record Post(
      Long id,
      String slug,
      Author author,
      String content,
      Long likeCount,
      Long commentCount,
      boolean bookmarked,
      LocalDateTime createdAt,
      LocalDateTime bookmarkedAt) {}

  @Builder
  public static record Author(Long id, String handle, String name, String profileImageUrl) {}
}
