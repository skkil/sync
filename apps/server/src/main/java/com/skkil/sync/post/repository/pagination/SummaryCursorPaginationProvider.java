package com.skkil.sync.post.repository.pagination;

import static com.skkil.sync.jooq.tables.Posts.POSTS;

import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import com.skkil.sync.post.dto.data.PostCursor;
import com.skkil.sync.post.dto.data.SummaryDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SummaryCursorPaginationProvider
    extends KeysetCursorPaginationProvider<SummaryDto, PostCursor> {

  @Override
  public Class<PostCursor> getCursorClass() {
    return PostCursor.class;
  }

  @Override
  protected List<KeysetField<PostCursor, ?>> getKeysetFields() {
    return List.of(KeysetField.asc(POSTS.ID, PostCursor::id));
  }

  @Override
  public PostCursor convert(SummaryDto entity) {
    return new PostCursor(entity.postId());
  }
}
