package com.skkil.sync.common.util.pagination.interfaces;

import com.skkil.sync.common.util.pagination.model.Cursor;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;

public interface CursorPaginationProvider<T, C extends Cursor> {

  Class<C> getCursorClass();

  Condition getNextCondition(C cursor);

  Condition getPreviousCondition(C cursor);

  List<OrderField<?>> getOrderFields();

  C convert(T entity);
}
