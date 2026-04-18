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

    List<OrderField<?>> orderFields = provider.getOrderFields();

    List<T> results = fetcher.fetch(condition, orderFields, requestedSize + 1);

    CursorPaginationResponse.PageInfo pageInfo =
        CursorPaginationResponse.PageInfo.builder()
            .size(Math.min(results.size(), requestedSize))
            .hasNextPage(results.size() == requestedSize + 1)
            .hasPreviousPage(cursor != null)
            .startCursor(
                !results.isEmpty()
                    ? cursorConverter.encode(provider.convert(results.get(0)))
                    : null)
            .endCursor(
                !results.isEmpty()
                    ? cursorConverter.encode(provider.convert(results.get(results.size() - 1)))
                    : null)
            .build();

    List<CursorPaginationResponse.Node<T>> nodes =
        results.stream()
            .limit(requestedSize)
            .map(
                result ->
                    new CursorPaginationResponse.Node<>(
                        cursorConverter.encode(provider.convert(result)), result))
            .toList();

    return new CursorPaginationResponse<>(pageInfo, nodes);
  }
}
