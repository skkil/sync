package com.skkil.sync.recommendation.repository.pagination;

import static com.skkil.sync.jooq.tables.Posts.POSTS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import com.skkil.sync.recommendation.dto.data.FeedCursor;
import com.skkil.sync.recommendation.dto.data.FeedDto;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.stereotype.Component;

@Component
public class FeedCursorPaginationProvider implements CursorPaginationProvider<FeedDto, FeedCursor> {

  @Override
  public Class<FeedCursor> getCursorClass() {
    return FeedCursor.class;
  }

  @Override
  public Condition getNextCondition(FeedCursor cursor) {
    return POSTS.ID.gt(cursor.id());
  }

  @Override
  public Condition getPreviousCondition(FeedCursor cursor) {
    return POSTS.ID.lt(cursor.id());
  }

  @Override
  public List<OrderField<?>> getOrderFields() {
    return List.of(POSTS.CREATED_AT.desc(), POSTS.ID.desc());
  }

  @Override
  public List<OrderField<?>> getReversedOrderFields() {
    return List.of(POSTS.CREATED_AT.asc(), POSTS.ID.asc());
  }

  @Override
  public FeedCursor convert(FeedDto entity) {
    return new FeedCursor(entity.id());
  }
}
