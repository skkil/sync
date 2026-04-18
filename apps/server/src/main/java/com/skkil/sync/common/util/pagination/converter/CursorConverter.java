package com.skkil.sync.common.util.pagination.converter;

import com.skkil.sync.common.util.pagination.exception.InvalidPaginationParametersException;
import com.skkil.sync.common.util.pagination.model.Cursor;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class CursorConverter {

  private final ObjectMapper objectMapper;

  public CursorConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String encode(Cursor cursor) {
    if (cursor == null) {
      return null;
    }

    try {
      String json = objectMapper.writeValueAsString(cursor.getFields());
      return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      log.debug("Failed to encode cursor: {}", cursor, e);
      throw new InvalidPaginationParametersException("Failed to encode cursor: " + cursor);
    }
  }

  public <C extends Cursor> C decode(String str, Class<C> type) {
    if (str == null || str.isEmpty()) {
      return null;
    }

    try {
      byte[] decodedBytes = Base64.getDecoder().decode(str);
      String json = new String(decodedBytes, StandardCharsets.UTF_8);
      return objectMapper.readValue(json, type);
    } catch (Exception e) {
      log.debug("Failed to decode cursor: {}", str, e);
      throw new InvalidPaginationParametersException("Invalid cursor format: " + str);
    }
  }
}
