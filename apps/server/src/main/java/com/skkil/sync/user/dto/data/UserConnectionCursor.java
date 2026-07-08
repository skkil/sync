package com.skkil.sync.user.dto.data;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.util.Map;

public record UserConnectionCursor(Long relationshipId) implements Cursor {

  @Override
  public Map<String, String> getFields() {
    return Map.of("relationshipId", String.valueOf(relationshipId));
  }
}
