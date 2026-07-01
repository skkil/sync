package com.skkil.sync.bookmark.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import java.time.OffsetDateTime;
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
      OffsetDateTime createdAt,
      OffsetDateTime bookmarkedAt) {}

  @Builder
  public static record Author(Long id, String handle, String name, String profileImageUrl) {}
}
