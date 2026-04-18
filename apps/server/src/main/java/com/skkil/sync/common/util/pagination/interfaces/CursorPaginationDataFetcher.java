package com.skkil.sync.common.util.pagination.interfaces;

import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;

@FunctionalInterface
public interface CursorPaginationDataFetcher<T> {

  List<T> fetch(Condition condition, List<OrderField<?>> orderFields, int size);
}
