package com.skkil.sync.bookmark.repository.pagination;

import static com.skkil.sync.jooq.tables.ReflectionBookmarks.REFLECTION_BOOKMARKS;

import com.skkil.sync.bookmark.dto.data.BookmarkedReflectionCursor;
import com.skkil.sync.bookmark.dto.data.BookmarkedReflectionDto;
import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.stereotype.Component;

@Component
public class BookmarkedReflectionCursorPaginationProvider
    implements CursorPaginationProvider<BookmarkedReflectionDto, BookmarkedReflectionCursor> {

  @Override
  public Class<BookmarkedReflectionCursor> getCursorClass() {
    return BookmarkedReflectionCursor.class;
  }

  @Override
  public Condition getNextCondition(BookmarkedReflectionCursor cursor) {
    return REFLECTION_BOOKMARKS
        .CREATED_AT
        .lt(cursor.bookmarkedAt())
        .or(
            REFLECTION_BOOKMARKS
                .CREATED_AT
                .eq(cursor.bookmarkedAt())
                .and(REFLECTION_BOOKMARKS.ID.lt(cursor.bookmarkId())));
  }

  @Override
  public Condition getPreviousCondition(BookmarkedReflectionCursor cursor) {
    return REFLECTION_BOOKMARKS
        .CREATED_AT
        .gt(cursor.bookmarkedAt())
        .or(
            REFLECTION_BOOKMARKS
                .CREATED_AT
                .eq(cursor.bookmarkedAt())
                .and(REFLECTION_BOOKMARKS.ID.gt(cursor.bookmarkId())));
  }

  @Override
  public List<OrderField<?>> getOrderFields() {
    return List.of(REFLECTION_BOOKMARKS.CREATED_AT.desc(), REFLECTION_BOOKMARKS.ID.desc());
  }

  @Override
  public List<OrderField<?>> getReversedOrderFields() {
    return List.of(REFLECTION_BOOKMARKS.CREATED_AT.asc(), REFLECTION_BOOKMARKS.ID.asc());
  }

  @Override
  public BookmarkedReflectionCursor convert(BookmarkedReflectionDto entity) {
    return new BookmarkedReflectionCursor(entity.bookmarkedAt(), entity.bookmarkId());
  }
}
