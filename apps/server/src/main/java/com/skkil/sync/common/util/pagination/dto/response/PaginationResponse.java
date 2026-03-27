package com.skkil.sync.common.util.pagination.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PaginationResponse<T>(List<T> content, boolean hasNext, boolean hasPrevious) {

  public static <T> PaginationResponse<T> empty() {
    return new PaginationResponse<>(List.of(), false, false);
  }
}
