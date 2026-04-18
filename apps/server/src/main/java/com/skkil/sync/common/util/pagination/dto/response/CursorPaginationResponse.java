package com.skkil.sync.common.util.pagination.dto.response;

import java.util.List;
import java.util.function.Function;
import lombok.Builder;

@Builder
public record CursorPaginationResponse<T>(PageInfo pageInfo, List<Node<T>> nodes) {

  @Builder
  public static record PageInfo(
      int size,
      boolean hasNextPage,
      boolean hasPreviousPage,
      String startCursor,
      String endCursor) {}

  @Builder
  public static record Node<T>(String cursor, T content) {}

  public <R> CursorPaginationResponse<R> map(Function<T, R> mapper) {

    return new CursorPaginationResponse<>(
        pageInfo,
        nodes.stream()
            .map(node -> new Node<>(node.cursor(), mapper.apply(node.content())))
            .toList());
  }
}
