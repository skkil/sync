package com.skkil.sync.post.dto.data;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.time.OffsetDateTime;
import java.util.Map;

public record BookmarkedPostCursor(OffsetDateTime bookmarkedAt, Long postId) implements Cursor {

  @Override
  public Map<String, String> getFields() {
    return Map.of(
        "bookmarkedAt", bookmarkedAt.toString(),
        "postId", postId.toString());
  }
}
