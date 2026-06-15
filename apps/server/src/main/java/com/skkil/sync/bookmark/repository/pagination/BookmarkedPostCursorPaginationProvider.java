package com.skkil.sync.bookmark.repository.pagination;

import static com.skkil.sync.jooq.tables.PostBookmarks.POST_BOOKMARKS;

import com.skkil.sync.bookmark.dto.data.BookmarkedPostCursor;
import com.skkil.sync.bookmark.dto.data.BookmarkedPostDto;
import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.stereotype.Component;

@Component
public class BookmarkedPostCursorPaginationProvider
    implements CursorPaginationProvider<BookmarkedPostDto, BookmarkedPostCursor> {

  @Override
  public Class<BookmarkedPostCursor> getCursorClass() {
    return BookmarkedPostCursor.class;
  }

  @Override
  public Condition getNextCondition(BookmarkedPostCursor cursor) {
    return POST_BOOKMARKS
        .CREATED_AT
        .lt(cursor.bookmarkedAt())
        .or(
            POST_BOOKMARKS
                .CREATED_AT
                .eq(cursor.bookmarkedAt())
                .and(POST_BOOKMARKS.ID.lt(cursor.bookmarkId())));
  }

  @Override
  public Condition getPreviousCondition(BookmarkedPostCursor cursor) {
    return POST_BOOKMARKS
        .CREATED_AT
        .gt(cursor.bookmarkedAt())
        .or(
            POST_BOOKMARKS
                .CREATED_AT
                .eq(cursor.bookmarkedAt())
                .and(POST_BOOKMARKS.ID.gt(cursor.bookmarkId())));
  }

  @Override
  public List<OrderField<?>> getOrderFields() {
    return List.of(POST_BOOKMARKS.CREATED_AT.desc(), POST_BOOKMARKS.ID.desc());
  }

  @Override
  public List<OrderField<?>> getReversedOrderFields() {
    return List.of(POST_BOOKMARKS.CREATED_AT.asc(), POST_BOOKMARKS.ID.asc());
  }

  @Override
  public BookmarkedPostCursor convert(BookmarkedPostDto entity) {
    return new BookmarkedPostCursor(entity.bookmarkedAt(), entity.bookmarkId());
  }
}
