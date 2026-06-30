package com.skkil.sync.common.util.pagination.keyset;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.util.function.Function;
import org.jooq.Field;
import org.jooq.SortOrder;

public record KeysetField<C extends Cursor, V>(
    Field<V> column, SortOrder order, Function<C, V> cursorValue) {

  public static <C extends Cursor, V> KeysetField<C, V> asc(
      Field<V> column, Function<C, V> cursorValue) {
    return new KeysetField<>(column, SortOrder.ASC, cursorValue);
  }

  public static <C extends Cursor, V> KeysetField<C, V> desc(
      Field<V> column, Function<C, V> cursorValue) {
    return new KeysetField<>(column, SortOrder.DESC, cursorValue);
  }
}
