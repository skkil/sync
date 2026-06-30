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
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaginationService {

  private final CursorConverter cursorConverter;

  public PaginationService(CursorConverter cursorConverter) {
    this.cursorConverter = cursorConverter;
  }

  public <T> OffsetPaginationResponse<T> paginate(
      OffsetPaginationDataFetcher<T> fetcher, OffsetPaginationRequest pagination) {
    log.debug("Fetching {} elements for page {}", pagination.size(), pagination.page());

    Pageable pageable = Pageable.ofSize(pagination.size()).withPage(pagination.page());
    Page<T> page = fetcher.fetch(pageable);

    OffsetPaginationResponse.PageInfo pageInfo =
        OffsetPaginationResponse.PageInfo.builder()
            .page(pagination.page())
            .size(pagination.size())
            .hasNextPage(page.hasNext())
            .hasPreviousPage(page.hasPrevious())
            .build();

    log.debug(
        "Fetched {} elements for page {} (hasNextPage={}, hasPreviousPage={})",
        page.getContent().size(),
        pagination.page(),
        pageInfo.hasNextPage(),
        pageInfo.hasPreviousPage());

    return new OffsetPaginationResponse<>(pageInfo, page.getContent());
  }

  public <T, C extends Cursor> CursorPaginationResponse<T> paginate(
      CursorPaginationDataFetcher<T> fetcher,
      CursorPaginationProvider<T, C> provider,
      CursorPaginationRequest pagination) {
    boolean isForward = pagination.isForward();
    String encodedCursor = isForward ? pagination.after() : pagination.before();
    int requestedSize = isForward ? pagination.first() : pagination.last();

    log.debug(
        "Fetching {} elements {} cursor {}",
        requestedSize,
        isForward ? "after" : "before",
        encodedCursor);

    C cursor = cursorConverter.decode(encodedCursor, provider.getCursorClass());

    Condition condition =
        cursor == null
            ? noCondition()
            : isForward ? provider.getNextCondition(cursor) : provider.getPreviousCondition(cursor);

    List<OrderField<?>> orderFields =
        isForward ? provider.getOrderFields() : provider.getReversedOrderFields();

    List<T> results = fetcher.fetch(condition, orderFields, requestedSize + 1);
    boolean hasExtraItem = results.size() > requestedSize;
    List<T> pageResults = hasExtraItem ? results.subList(0, requestedSize) : results;

    log.debug(
        "Fetched {} elements {} cursor {} (hasExtraItem={})",
        pageResults.size(),
        isForward ? "after" : "before",
        encodedCursor,
        hasExtraItem);

    List<CursorPaginationResponse.Node<T>> nodes =
        new ArrayList<>(
            pageResults.stream()
                .map(
                    result ->
                        new CursorPaginationResponse.Node<>(
                            cursorConverter.encode(provider.convert(result)), result))
                .toList());

    if (!isForward) {
      Collections.reverse(nodes);
    }

    T boundaryItem = pageResults.isEmpty() ? null : pageResults.get(0);

    boolean hasNextPage;
    boolean hasPreviousPage;
    if (isForward) {
      hasNextPage = hasExtraItem;
      hasPreviousPage =
          cursor != null
              && boundaryItem != null
              && hasAdjacentResult(
                  fetcher,
                  provider.getPreviousCondition(provider.convert(boundaryItem)),
                  provider.getReversedOrderFields());
    } else {
      hasPreviousPage = hasExtraItem;
      hasNextPage =
          cursor != null
              && boundaryItem != null
              && hasAdjacentResult(
                  fetcher,
                  provider.getNextCondition(provider.convert(boundaryItem)),
                  provider.getOrderFields());
    }

    CursorPaginationResponse.PageInfo pageInfo =
        CursorPaginationResponse.PageInfo.builder()
            .size(pageResults.size())
            .hasNextPage(hasNextPage)
            .hasPreviousPage(hasPreviousPage)
            .startCursor(!nodes.isEmpty() ? nodes.get(0).cursor() : null)
            .endCursor(!nodes.isEmpty() ? nodes.get(nodes.size() - 1).cursor() : null)
            .build();

    log.debug(
        "Returning {} nodes (hasNextPage={}, hasPreviousPage={}, startCursor={}, endCursor={})",
        nodes.size(),
        hasNextPage,
        hasPreviousPage,
        pageInfo.startCursor(),
        pageInfo.endCursor());

    return new CursorPaginationResponse<>(pageInfo, nodes);
  }

  private <T> boolean hasAdjacentResult(
      CursorPaginationDataFetcher<T> fetcher,
      Condition condition,
      List<OrderField<?>> orderFields) {
    return !fetcher.fetch(condition, orderFields, 1).isEmpty();
  }
}
