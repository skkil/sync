package com.skkil.sync.common.util.pagination.converter;

import com.skkil.sync.common.util.pagination.exception.InvalidPaginationParametersException;
import com.skkil.sync.common.util.pagination.model.Cursor;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class CursorConverter {

  private final ObjectMapper objectMapper;

  public CursorConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String encode(Cursor cursor) {
    try {
      String json = objectMapper.writeValueAsString(cursor);
      return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      throw new RuntimeException("Failed to encode cursor", e);
    }
  }

  public Cursor decode(String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }

    try {
      byte[] decodedBytes = Base64.getDecoder().decode(str);
      String json = new String(decodedBytes, StandardCharsets.UTF_8);
      return objectMapper.readValue(json, Cursor.class);
    } catch (Exception e) {
      throw new InvalidPaginationParametersException("Invalid cursor format", e);
    }
  }
}
