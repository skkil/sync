package com.skkil.sync.bookmark.dto.data;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.time.LocalDateTime;
import java.util.Map;

public record BookmarkedReflectionCursor(LocalDateTime bookmarkedAt, Long bookmarkId)
    implements Cursor {

  @Override
  public Map<String, String> getFields() {
    return Map.of(
        "bookmarkedAt", bookmarkedAt.toString(),
        "bookmarkId", bookmarkId.toString());
  }
}
