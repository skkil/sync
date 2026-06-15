package com.skkil.sync.bookmark.repository;

import static com.skkil.sync.jooq.tables.PostBookmarks.POST_BOOKMARKS;
import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.bookmark.dto.data.BookmarkedPostDto;
import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class PostBookmarkQueryRepository {

  private final DSLContext dsl;

  public PostBookmarkQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<BookmarkedPostDto> getBookmarkedPosts(Long userId) {
    return (condition, orderFields, size) ->
        dsl.select(
                POSTS.ID.as("id"),
                POSTS.SLUG.as("slug"),
                POST_BOOKMARKS.ID.as("bookmarkId"),
                POST_BOOKMARKS.CREATED_AT.as("bookmarkedAt"),
                POSTS.AUTHOR_ID.as("authorId"),
                USERS.HANDLE.as("authorHandle"),
                USERS.FULL_NAME.as("authorName"),
                USERS.PROFILE_IMAGE_ID.as("authorProfileImageId"),
                POSTS.CONTENT.as("content"),
                DSL.value(0L).as("likeCount"),
                DSL.value(0L).as("commentCount"),
                DSL.value(true).as("bookmarked"),
                POSTS.CREATED_AT.as("createdAt"),
                POSTS.UPDATED_AT.as("updatedAt"))
            .from(POST_BOOKMARKS)
            .join(POSTS)
            .on(POST_BOOKMARKS.POST_ID.eq(POSTS.ID))
            .join(USERS)
            .on(POSTS.AUTHOR_ID.eq(USERS.ID))
            .where(condition.and(POST_BOOKMARKS.USER_ID.eq(userId)))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(BookmarkedPostDto.class);
  }
}
