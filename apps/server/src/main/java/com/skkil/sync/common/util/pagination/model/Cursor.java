package com.skkil.sync.common.util.pagination.model;

import java.time.Instant;
import java.util.Comparator;
import lombok.Builder;

@Builder
public record Cursor(Long score, Instant createdAt, Instant updatedAt, Long id) {

  public static Comparator<Cursor> comparator() {
    return Comparator.comparing(Cursor::score)
        .thenComparing(Cursor::createdAt)
        .thenComparing(Cursor::updatedAt)
        .thenComparing(Cursor::id);
  }
}
