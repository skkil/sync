package com.skkil.sync.comment.repository.pagination;

import static com.skkil.sync.jooq.tables.Comments.COMMENTS;

import com.skkil.sync.comment.dto.data.CommentCursor;
import com.skkil.sync.comment.dto.data.CommentDto;
import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CommentCursorPaginationProvider
    extends KeysetCursorPaginationProvider<CommentDto, CommentCursor> {

  @Override
  public Class<CommentCursor> getCursorClass() {
    return CommentCursor.class;
  }

  @Override
  protected List<KeysetField<CommentCursor, ?>> getKeysetFields() {
    return List.of(
        KeysetField.desc(COMMENTS.CREATED_AT, CommentCursor::createdAt),
        KeysetField.desc(COMMENTS.ID, CommentCursor::id));
  }

  @Override
  public CommentCursor convert(CommentDto entity) {
    return new CommentCursor(entity.createdAt(), entity.id());
  }
}
