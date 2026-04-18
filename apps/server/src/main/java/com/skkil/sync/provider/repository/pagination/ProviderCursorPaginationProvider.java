package com.skkil.sync.provider.repository.pagination;

import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import com.skkil.sync.provider.dto.data.ProviderCursor;
import com.skkil.sync.provider.dto.data.ProviderDto;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.stereotype.Component;

@Component
public class ProviderCursorPaginationProvider
    implements CursorPaginationProvider<ProviderDto, ProviderCursor> {

  @Override
  public Class<ProviderCursor> getCursorClass() {
    return ProviderCursor.class;
  }

  @Override
  public Condition getNextCondition(ProviderCursor cursor) {
    return PROVIDERS.ID.gt(cursor.id());
  }

  @Override
  public Condition getPreviousCondition(ProviderCursor cursor) {
    return PROVIDERS.ID.lt(cursor.id());
  }

  @Override
  public List<OrderField<?>> getOrderFields() {
    return List.of(PROVIDERS.ID.asc());
  }

  @Override
  public List<OrderField<?>> getReversedOrderFields() {
    return List.of(PROVIDERS.ID.desc());
  }

  @Override
  public ProviderCursor convert(ProviderDto entity) {
    return new ProviderCursor(entity.id());
  }
}
