package com.skkil.sync.recommendation.repository.pagination;

import static com.skkil.sync.jooq.tables.Posts.POSTS;

import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import com.skkil.sync.recommendation.dto.data.FeedCursor;
import com.skkil.sync.recommendation.dto.data.FeedDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FeedCursorPaginationProvider
    extends KeysetCursorPaginationProvider<FeedDto, FeedCursor> {

  @Override
  public Class<FeedCursor> getCursorClass() {
    return FeedCursor.class;
  }

  @Override
  protected List<KeysetField<FeedCursor, ?>> getKeysetFields() {
    return List.of(
        KeysetField.desc(POSTS.CREATED_AT, FeedCursor::createdAt),
        KeysetField.desc(POSTS.ID, FeedCursor::id));
  }

  @Override
  public FeedCursor convert(FeedDto entity) {
    return new FeedCursor(entity.createdAt(), entity.id());
  }
}
