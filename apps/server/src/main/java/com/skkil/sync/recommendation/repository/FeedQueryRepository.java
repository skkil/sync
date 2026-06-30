package com.skkil.sync.recommendation.repository;

import static com.skkil.sync.jooq.tables.PostBookmarks.POST_BOOKMARKS;
import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.post.model.PostVisibility;
import com.skkil.sync.recommendation.dto.data.FeedDto;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class FeedQueryRepository {

  private final DSLContext dsl;

  public FeedQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<FeedDto> getRecentPosts(Long requesterId) {
    return (condition, orderFields, size) -> {
      return dsl.select(
              POSTS.ID.as("id"),
              POSTS.SLUG.as("slug"),
              POSTS.AUTHOR_ID.as("authorId"),
              USERS.HANDLE.as("authorHandle"),
              USERS.FULL_NAME.as("authorName"),
              USERS.PROFILE_IMAGE_ID.as("authorProfileImageId"),
              POSTS.CONTENT.as("content"),
              DSL.value(0).as("likeCount"),
              DSL.value(0).as("commentCount"),
              bookmarkedField(requesterId),
              POSTS.CREATED_AT.as("createdAt"),
              POSTS.UPDATED_AT.as("updatedAt"))
          .from(POSTS)
          .join(USERS)
          .on(POSTS.AUTHOR_ID.eq(USERS.ID))
          .where(condition.and(POSTS.VISIBILITY.eq(PostVisibility.VISIBLE.name())))
          .orderBy(orderFields)
          .limit(size)
          .fetchInto(FeedDto.class);
    };
  }

  private Field<Boolean> bookmarkedField(Long requesterId) {
    if (requesterId == null) {
      return DSL.value(false).as("bookmarked");
    }

    return DSL.field(
            DSL.exists(
                DSL.selectOne()
                    .from(POST_BOOKMARKS)
                    .where(POST_BOOKMARKS.POST_ID.eq(POSTS.ID))
                    .and(POST_BOOKMARKS.USER_ID.eq(requesterId))))
        .as("bookmarked");
  }
}
