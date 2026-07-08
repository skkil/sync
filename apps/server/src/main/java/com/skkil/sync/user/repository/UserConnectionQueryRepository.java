package com.skkil.sync.user.repository;

import static com.skkil.sync.jooq.tables.UserFollowRelationships.USER_FOLLOW_RELATIONSHIPS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.user.dto.data.UserConnectionDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserConnectionQueryRepository {

  private final DSLContext dsl;

  public UserConnectionQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<UserConnectionDto> getFollowing(Long userId) {
    return (condition, orderFields, size) ->
        dsl.select(
                USER_FOLLOW_RELATIONSHIPS.ID.as("relationshipId"),
                USER_FOLLOW_RELATIONSHIPS.FOLLOWEE_ID.as("userId"))
            .from(USER_FOLLOW_RELATIONSHIPS)
            .where(condition.and(USER_FOLLOW_RELATIONSHIPS.FOLLOWER_ID.eq(userId)))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(UserConnectionDto.class);
  }

  public CursorPaginationDataFetcher<UserConnectionDto> getFollowers(Long userId) {
    return (condition, orderFields, size) ->
        dsl.select(
                USER_FOLLOW_RELATIONSHIPS.ID.as("relationshipId"),
                USER_FOLLOW_RELATIONSHIPS.FOLLOWER_ID.as("userId"))
            .from(USER_FOLLOW_RELATIONSHIPS)
            .where(condition.and(USER_FOLLOW_RELATIONSHIPS.FOLLOWEE_ID.eq(userId)))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(UserConnectionDto.class);
  }
}
