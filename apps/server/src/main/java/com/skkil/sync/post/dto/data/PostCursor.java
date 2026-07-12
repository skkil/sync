package com.skkil.sync.post.dto.data;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.time.OffsetDateTime;
import java.util.Map;

public record PostCursor(OffsetDateTime sortKey, Long postId) implements Cursor {

  @Override
  public Map<String, String> getFields() {
    return Map.of(
        "sortKey", sortKey.toString(),
        "postId", postId.toString());
  }
}
