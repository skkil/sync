package com.skkil.sync.post.repository.pagination;

import static com.skkil.sync.jooq.tables.PostBookmarks.POST_BOOKMARKS;
import static com.skkil.sync.jooq.tables.Posts.POSTS;

import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import com.skkil.sync.post.dto.data.PostCursor;
import com.skkil.sync.post.dto.data.PostDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookmarkedPostCursorPaginationProvider
    extends KeysetCursorPaginationProvider<PostDto, PostCursor> {

  @Override
  public Class<PostCursor> getCursorClass() {
    return PostCursor.class;
  }

  @Override
  protected List<KeysetField<PostCursor, ?>> getKeysetFields() {
    return List.of(
        KeysetField.desc(POST_BOOKMARKS.CREATED_AT, PostCursor::sortKey),
        KeysetField.desc(POSTS.ID, PostCursor::postId));
  }

  @Override
  public PostCursor convert(PostDto entity) {
    return new PostCursor(entity.sortKey(), entity.id());
  }
}
