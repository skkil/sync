package com.skkil.sync.common.util.pagination.service;

import static org.jooq.impl.DSL.noCondition;

import com.skkil.sync.common.util.pagination.converter.CursorConverter;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.common.util.pagination.dto.response.OffsetPaginationResponse;
import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import com.skkil.sync.common.util.pagination.interfaces.OffsetPaginationDataFetcher;
import com.skkil.sync.common.util.pagination.model.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PaginationService {

  private final CursorConverter cursorConverter;

  public PaginationService(CursorConverter cursorConverter) {
    this.cursorConverter = cursorConverter;
  }

  public <T> OffsetPaginationResponse<T> paginate(
      OffsetPaginationDataFetcher<T> fetcher, OffsetPaginationRequest pagination) {
    Pageable pageable = Pageable.ofSize(pagination.size()).withPage(pagination.page());
    Page<T> page = fetcher.fetch(pageable);

    OffsetPaginationResponse.PageInfo pageInfo =
        OffsetPaginationResponse.PageInfo.builder()
            .page(pagination.page())
            .size(pagination.size())
            .hasNextPage(page.hasNext())
            .hasPreviousPage(page.hasPrevious())
            .build();

    return new OffsetPaginationResponse<>(pageInfo, page.getContent());
  }

  public <T, C extends Cursor> CursorPaginationResponse<T> paginate(
      CursorPaginationDataFetcher<T> fetcher,
      CursorPaginationProvider<T, C> provider,
      CursorPaginationRequest pagination) {
    boolean isForward = pagination.isForward();
    String encodedCursor = isForward ? pagination.after() : pagination.before();
    int requestedSize = isForward ? pagination.first() : pagination.last();

    C cursor = cursorConverter.decode(encodedCursor, provider.getCursorClass());

    Condition condition =
        cursor == null
            ? noCondition()
            : isForward ? provider.getNextCondition(cursor) : provider.getPreviousCondition(cursor);

    List<OrderField<?>> orderFields =
        isForward ? provider.getOrderFields() : provider.getReversedOrderFields();

    List<T> results = fetcher.fetch(condition, orderFields, requestedSize + 1);

    List<CursorPaginationResponse.Node<T>> nodes =
        new ArrayList<>(
            results.stream()
                .limit(requestedSize)
                .map(
                    result ->
                        new CursorPaginationResponse.Node<>(
                            cursorConverter.encode(provider.convert(result)), result))
                .toList());

    if (!isForward) {
      Collections.reverse(nodes);
    }

    boolean hasExtraItem = results.size() > requestedSize;
    boolean hasNextPage = isForward ? hasExtraItem : cursor != null;
    boolean hasPreviousPage = isForward ? cursor != null : hasExtraItem;

    CursorPaginationResponse.PageInfo pageInfo =
        CursorPaginationResponse.PageInfo.builder()
            .size(Math.min(results.size(), requestedSize))
            .hasNextPage(hasNextPage)
            .hasPreviousPage(hasPreviousPage)
            .startCursor(!nodes.isEmpty() ? nodes.get(0).cursor() : null)
            .endCursor(!nodes.isEmpty() ? nodes.get(nodes.size() - 1).cursor() : null)
            .build();

    return new CursorPaginationResponse<>(pageInfo, nodes);
  }
}
