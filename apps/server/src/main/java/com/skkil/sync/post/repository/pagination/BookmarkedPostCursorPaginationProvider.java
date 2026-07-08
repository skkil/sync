package com.skkil.sync.post.repository.pagination;

import static com.skkil.sync.jooq.tables.PostBookmarks.POST_BOOKMARKS;
import static com.skkil.sync.jooq.tables.Posts.POSTS;

import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import com.skkil.sync.post.dto.data.BookmarkedPostCursor;
import com.skkil.sync.post.dto.data.PostDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookmarkedPostCursorPaginationProvider
    extends KeysetCursorPaginationProvider<PostDto, BookmarkedPostCursor> {

  @Override
  public Class<BookmarkedPostCursor> getCursorClass() {
    return BookmarkedPostCursor.class;
  }

  @Override
  protected List<KeysetField<BookmarkedPostCursor, ?>> getKeysetFields() {
    return List.of(
        KeysetField.desc(POST_BOOKMARKS.CREATED_AT, BookmarkedPostCursor::bookmarkedAt),
        KeysetField.desc(POSTS.ID, BookmarkedPostCursor::postId));
  }

  @Override
  public BookmarkedPostCursor convert(PostDto entity) {
    return new BookmarkedPostCursor(entity.bookmarkedAt(), entity.id());
  }
}
