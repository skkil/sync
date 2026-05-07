package com.skkil.sync.bookmark.repository;

import static com.skkil.sync.jooq.tables.ReflectionBookmarks.REFLECTION_BOOKMARKS;
import static com.skkil.sync.jooq.tables.Reflections.REFLECTIONS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.bookmark.dto.data.BookmarkedReflectionDto;
import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class ReflectionBookmarkQueryRepository {

  private final DSLContext dsl;

  public ReflectionBookmarkQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<BookmarkedReflectionDto> getBookmarkedReflections(
      Long userId) {
    return (condition, orderFields, size) ->
        dsl.select(
                REFLECTIONS.ID.as("id"),
                REFLECTIONS.SLUG.as("slug"),
                REFLECTION_BOOKMARKS.ID.as("bookmarkId"),
                REFLECTION_BOOKMARKS.CREATED_AT.as("bookmarkedAt"),
                REFLECTIONS.AUTHOR_ID.as("authorId"),
                USERS.HANDLE.as("authorHandle"),
                USERS.FULL_NAME.as("authorName"),
                USERS.PROFILE_IMAGE_ID.as("authorProfileImageId"),
                REFLECTIONS.CONTENT.as("content"),
                DSL.value(0L).as("likeCount"),
                DSL.value(0L).as("commentCount"),
                DSL.value(true).as("bookmarked"),
                REFLECTIONS.CREATED_AT.as("createdAt"),
                REFLECTIONS.UPDATED_AT.as("updatedAt"))
            .from(REFLECTION_BOOKMARKS)
            .join(REFLECTIONS)
            .on(REFLECTION_BOOKMARKS.REFLECTION_ID.eq(REFLECTIONS.ID))
            .join(USERS)
            .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
            .where(condition.and(REFLECTION_BOOKMARKS.USER_ID.eq(userId)))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(BookmarkedReflectionDto.class);
  }
}
