package com.skkil.sync.common.util.pagination.dto.response;

import java.util.List;
import java.util.function.Function;
import lombok.Builder;

@Builder
public record OffsetPaginationResponse<T>(PageInfo pageInfo, List<T> content) {

  @Builder
  public static record PageInfo(int page, int size, boolean hasNextPage, boolean hasPreviousPage) {}

  public <R> OffsetPaginationResponse<R> map(Function<T, R> mapper) {
    return new OffsetPaginationResponse<>(pageInfo, content.stream().map(mapper).toList());
  }
}
