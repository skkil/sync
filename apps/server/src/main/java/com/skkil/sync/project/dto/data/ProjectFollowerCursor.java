package com.skkil.sync.project.dto.data;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.util.Map;

public record ProjectFollowerCursor(Long relationshipId) implements Cursor {

  @Override
  public Map<String, String> getFields() {
    return Map.of("relationshipId", String.valueOf(relationshipId));
  }
}
