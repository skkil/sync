package com.skkil.sync.reflection.repository.pagination;

import static com.skkil.sync.jooq.tables.Reflections.REFLECTIONS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import com.skkil.sync.reflection.dto.data.ReflectionCursor;
import com.skkil.sync.reflection.dto.data.SummaryDto;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.stereotype.Component;

@Component
public class SummaryCursorPaginationProvider
    implements CursorPaginationProvider<SummaryDto, ReflectionCursor> {

  @Override
  public Class<ReflectionCursor> getCursorClass() {
    return ReflectionCursor.class;
  }

  @Override
  public Condition getNextCondition(ReflectionCursor cursor) {
    return REFLECTIONS.ID.gt(cursor.id());
  }

  @Override
  public Condition getPreviousCondition(ReflectionCursor cursor) {
    return REFLECTIONS.ID.lt(cursor.id());
  }

  @Override
  public List<OrderField<?>> getOrderFields() {
    return List.of(REFLECTIONS.ID.asc());
  }

  @Override
  public List<OrderField<?>> getReversedOrderFields() {
    return List.of(REFLECTIONS.ID.desc());
  }

  @Override
  public ReflectionCursor convert(SummaryDto entity) {
    return new ReflectionCursor(entity.reflectionId());
  }
}
