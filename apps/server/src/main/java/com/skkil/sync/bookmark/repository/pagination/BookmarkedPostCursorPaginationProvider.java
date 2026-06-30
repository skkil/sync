package com.skkil.sync.bookmark.repository.pagination;

import static com.skkil.sync.jooq.tables.PostBookmarks.POST_BOOKMARKS;

import com.skkil.sync.bookmark.dto.data.BookmarkedPostCursor;
import com.skkil.sync.bookmark.dto.data.BookmarkedPostDto;
import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookmarkedPostCursorPaginationProvider
    extends KeysetCursorPaginationProvider<BookmarkedPostDto, BookmarkedPostCursor> {

  @Override
  public Class<BookmarkedPostCursor> getCursorClass() {
    return BookmarkedPostCursor.class;
  }

  @Override
  protected List<KeysetField<BookmarkedPostCursor, ?>> getKeysetFields() {
    return List.of(
        KeysetField.desc(POST_BOOKMARKS.CREATED_AT, BookmarkedPostCursor::bookmarkedAt),
        KeysetField.desc(POST_BOOKMARKS.ID, BookmarkedPostCursor::bookmarkId));
  }

  @Override
  public BookmarkedPostCursor convert(BookmarkedPostDto entity) {
    return new BookmarkedPostCursor(entity.bookmarkedAt(), entity.bookmarkId());
  }
}
