package com.skkil.sync.recommendation.dto.data;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.time.LocalDateTime;
import java.util.Map;

public record FeedCursor(LocalDateTime createdAt, Long id) implements Cursor {

  @Override
  public Map<String, String> getFields() {
    return Map.of(
        "createdAt", createdAt.toString(),
        "id", String.valueOf(id));
  }
}
