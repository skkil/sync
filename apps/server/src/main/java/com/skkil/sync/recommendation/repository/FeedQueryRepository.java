package com.skkil.sync.recommendation.repository;

import static com.skkil.sync.jooq.tables.ReflectionBookmarks.REFLECTION_BOOKMARKS;
import static com.skkil.sync.jooq.tables.Reflections.REFLECTIONS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
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

  public CursorPaginationDataFetcher<FeedDto> getRecentReflections(Long requesterId) {
    return (condition, orderFields, size) -> {
      return dsl.select(
              REFLECTIONS.ID.as("id"),
              REFLECTIONS.SLUG.as("slug"),
              REFLECTIONS.AUTHOR_ID.as("authorId"),
              USERS.HANDLE.as("authorHandle"),
              USERS.FULL_NAME.as("authorName"),
              USERS.PROFILE_IMAGE_ID.as("authorProfileImageId"),
              REFLECTIONS.CONTENT.as("content"),
              DSL.value(0).as("likeCount"),
              DSL.value(0).as("commentCount"),
              bookmarkedField(requesterId),
              REFLECTIONS.CREATED_AT.as("createdAt"),
              REFLECTIONS.UPDATED_AT.as("updatedAt"))
          .from(REFLECTIONS)
          .join(USERS)
          .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
          .where(condition)
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
                    .from(REFLECTION_BOOKMARKS)
                    .where(REFLECTION_BOOKMARKS.REFLECTION_ID.eq(REFLECTIONS.ID))
                    .and(REFLECTION_BOOKMARKS.USER_ID.eq(requesterId))))
        .as("bookmarked");
  }
}
