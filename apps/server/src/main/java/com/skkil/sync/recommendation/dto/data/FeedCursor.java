package com.skkil.sync.recommendation.dto.data;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.util.Map;

public record FeedCursor(Long id) implements Cursor {

  @Override
  public Map<String, String> getFields() {
    return Map.of("id", String.valueOf(id));
  }
}
