package com.skkil.sync.common.util.pagination.dto.response;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Builder;

@Builder
public record CursorPaginationResponse<T>(PageInfo pageInfo, List<Node<T>> nodes)
    implements Iterable<T> {

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

  @Override
  public Iterator<T> iterator() {
    return nodes.stream().map(Node::content).iterator();
  }

  public <K, V, R> CursorPaginationResponse<R> mapWithLookup(
      Function<T, K> keyExtractor,
      Function<List<K>, Map<K, V>> lookup,
      BiFunction<T, Map<K, V>, R> merge) {
    List<K> keys =
        nodes.stream()
            .map(Node::content)
            .map(keyExtractor)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

    Map<K, V> lookupResult = lookup.apply(keys);

    return map(item -> merge.apply(item, lookupResult));
  }
}
