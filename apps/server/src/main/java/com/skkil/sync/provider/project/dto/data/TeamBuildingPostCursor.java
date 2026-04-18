package com.skkil.sync.provider.project.dto.data;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.util.Map;

public record TeamBuildingPostCursor(Long id) implements Cursor {

  @Override
  public Map<String, String> getFields() {
    return Map.of("id", id.toString());
  }
}
