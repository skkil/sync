package com.skkil.sync.provider.repository.pagination;

import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;

import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import com.skkil.sync.provider.dto.data.ProviderCursor;
import com.skkil.sync.provider.dto.data.ProviderDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProviderCursorPaginationProvider
    extends KeysetCursorPaginationProvider<ProviderDto, ProviderCursor> {

  @Override
  public Class<ProviderCursor> getCursorClass() {
    return ProviderCursor.class;
  }

  @Override
  protected List<KeysetField<ProviderCursor, ?>> getKeysetFields() {
    return List.of(KeysetField.asc(PROVIDERS.ID, ProviderCursor::id));
  }

  @Override
  public ProviderCursor convert(ProviderDto entity) {
    return new ProviderCursor(entity.id());
  }
}
