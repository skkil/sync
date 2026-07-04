package com.skkil.sync.comment.dto.data;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.time.OffsetDateTime;
import java.util.Map;

public record CommentCursor(OffsetDateTime createdAt, Long id) implements Cursor {

  @Override
  public Map<String, String> getFields() {
    return Map.of("createdAt", createdAt.toString(), "id", id.toString());
  }
}
