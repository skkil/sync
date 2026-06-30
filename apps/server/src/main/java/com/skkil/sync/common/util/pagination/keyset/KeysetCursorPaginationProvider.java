package com.skkil.sync.common.util.pagination.keyset;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import com.skkil.sync.common.util.pagination.model.Cursor;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.jooq.SortOrder;
import org.jooq.impl.DSL;

public abstract class KeysetCursorPaginationProvider<T, C extends Cursor>
    implements CursorPaginationProvider<T, C> {

  protected abstract List<KeysetField<C, ?>> getKeysetFields();

  @Override
  public List<OrderField<?>> getOrderFields() {
    return getKeysetFields().stream()
        .<OrderField<?>>map(field -> toOrderField(field, field.order()))
        .toList();
  }

  @Override
  public List<OrderField<?>> getReversedOrderFields() {
    return getKeysetFields().stream()
        .<OrderField<?>>map(field -> toOrderField(field, reverse(field.order())))
        .toList();
  }

  @Override
  public Condition getNextCondition(C cursor) {
    return buildSeekCondition(cursor, true);
  }

  @Override
  public Condition getPreviousCondition(C cursor) {
    return buildSeekCondition(cursor, false);
  }

  private Condition buildSeekCondition(C cursor, boolean forward) {
    List<KeysetField<C, ?>> fields = getKeysetFields();

    Condition seekCondition = DSL.falseCondition();
    for (int i = 0; i < fields.size(); i++) {
      Condition precedingFieldsMatch = DSL.noCondition();
      for (int j = 0; j < i; j++) {
        precedingFieldsMatch = precedingFieldsMatch.and(equalsCondition(fields.get(j), cursor));
      }

      seekCondition =
          seekCondition.or(
              precedingFieldsMatch.and(progressesCondition(fields.get(i), cursor, forward)));
    }

    return seekCondition;
  }

  private <V> Condition equalsCondition(KeysetField<C, V> field, C cursor) {
    return field.column().eq(field.cursorValue().apply(cursor));
  }

  private <V> Condition progressesCondition(KeysetField<C, V> field, C cursor, boolean forward) {
    V value = field.cursorValue().apply(cursor);
    boolean isGreaterThan = forward == (field.order() == SortOrder.ASC);
    return isGreaterThan ? field.column().gt(value) : field.column().lt(value);
  }

  private static <V> OrderField<?> toOrderField(KeysetField<?, V> field, SortOrder order) {
    return order == SortOrder.ASC ? field.column().asc() : field.column().desc();
  }

  private static SortOrder reverse(SortOrder order) {
    return order == SortOrder.ASC ? SortOrder.DESC : SortOrder.ASC;
  }
}
