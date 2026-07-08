package com.skkil.sync.post.dto.data;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.time.OffsetDateTime;
import java.util.Map;

public record PostRecommendationCursor(OffsetDateTime createdAt, Long likeCount, Long id)
    implements Cursor {

  @Override
  public Map<String, String> getFields() {
    return Map.of(
        "createdAt", createdAt.toString(),
        "likeCount", String.valueOf(likeCount),
        "id", String.valueOf(id));
  }
}
