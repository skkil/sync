package com.skkil.sync.recommendation.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import java.time.LocalDateTime;
import lombok.Builder;

public record GetFeedResponse(CursorPaginationResponse<FeedItem> items) {

  @Builder
  public static record FeedItem(
      Long id,
      String slug,
      Author author,
      String content,
      Long likeCount,
      Long commentCount,
      boolean bookmarked,
      LocalDateTime createdAt) {}

  @Builder
  public static record Author(Long id, String handle, String name, String profileImageUrl) {}
}
