package com.skkil.sync.common.util.pagination.dto.response;

import java.util.Collections;
import java.util.List;
import lombok.Builder;

@Builder
public record CursorPaginationResponse<T>(List<T> content, boolean hasNext, String nextCursor) {

  public static <T> CursorPaginationResponse<T> empty() {
    return new CursorPaginationResponse<>(Collections.emptyList(), false, null);
  }
}
