package com.skkil.sync.post.repository.pagination;

import static com.skkil.sync.jooq.tables.Posts.POSTS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import com.skkil.sync.post.dto.data.PostCursor;
import com.skkil.sync.post.dto.data.SummaryDto;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.stereotype.Component;

@Component
public class SummaryCursorPaginationProvider
    implements CursorPaginationProvider<SummaryDto, PostCursor> {

  @Override
  public Class<PostCursor> getCursorClass() {
    return PostCursor.class;
  }

  @Override
  public Condition getNextCondition(PostCursor cursor) {
    return POSTS.ID.gt(cursor.id());
  }

  @Override
  public Condition getPreviousCondition(PostCursor cursor) {
    return POSTS.ID.lt(cursor.id());
  }

  @Override
  public List<OrderField<?>> getOrderFields() {
    return List.of(POSTS.ID.asc());
  }

  @Override
  public List<OrderField<?>> getReversedOrderFields() {
    return List.of(POSTS.ID.desc());
  }

  @Override
  public PostCursor convert(SummaryDto entity) {
    return new PostCursor(entity.postId());
  }
}
