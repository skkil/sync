package com.skkil.sync.user.repository;

import static com.skkil.sync.jooq.tables.ProjectFollowRelationships.PROJECT_FOLLOW_RELATIONSHIPS;
import static com.skkil.sync.jooq.tables.UserFollowRelationships.USER_FOLLOW_RELATIONSHIPS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class UserRecommendationQueryRepository {

  private final DSLContext dsl;

  public UserRecommendationQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<Long> findProjectOverlapCandidateIds(Long userId, int limit) {
    var mine = PROJECT_FOLLOW_RELATIONSHIPS.as("mine");
    var other = PROJECT_FOLLOW_RELATIONSHIPS.as("other");

    return dsl.select(other.FOLLOWER_ID)
        .from(mine)
        .join(other)
        .on(other.PROJECT_ID.eq(mine.PROJECT_ID), other.FOLLOWER_ID.ne(userId))
        .where(mine.FOLLOWER_ID.eq(userId))
        .andNotExists(
            dsl.selectOne()
                .from(USER_FOLLOW_RELATIONSHIPS)
                .where(
                    USER_FOLLOW_RELATIONSHIPS.FOLLOWER_ID.eq(userId),
                    USER_FOLLOW_RELATIONSHIPS.FOLLOWEE_ID.eq(other.FOLLOWER_ID)))
        .groupBy(other.FOLLOWER_ID)
        .orderBy(DSL.count().desc())
        .limit(limit)
        .fetch(other.FOLLOWER_ID);
  }

  public List<Long> findTrendingCandidateIds(Long userId, int limit) {
    OffsetDateTime since = OffsetDateTime.now(ZoneOffset.UTC).minusDays(30);
    var mine = USER_FOLLOW_RELATIONSHIPS.as("mine");

    return dsl.select(USER_FOLLOW_RELATIONSHIPS.FOLLOWEE_ID)
        .from(USER_FOLLOW_RELATIONSHIPS)
        .where(USER_FOLLOW_RELATIONSHIPS.CREATED_AT.ge(since))
        .and(USER_FOLLOW_RELATIONSHIPS.FOLLOWEE_ID.ne(userId))
        .andNotExists(
            dsl.selectOne()
                .from(mine)
                .where(
                    mine.FOLLOWER_ID.eq(userId),
                    mine.FOLLOWEE_ID.eq(USER_FOLLOW_RELATIONSHIPS.FOLLOWEE_ID)))
        .groupBy(USER_FOLLOW_RELATIONSHIPS.FOLLOWEE_ID)
        .orderBy(DSL.count().desc())
        .limit(limit)
        .fetch(USER_FOLLOW_RELATIONSHIPS.FOLLOWEE_ID);
  }

  // 신호 기반 후보만으로 추천 인원이 부족할 때(콜드 스타트) 채워 넣을 대체 후보 목록
  public List<Long> findRecentlyJoinedCandidateIds(Long userId, int limit) {
    return dsl.select(USERS.ID)
        .from(USERS)
        .where(USERS.ID.ne(userId), USERS.DELETED_AT.isNull())
        .andNotExists(
            dsl.selectOne()
                .from(USER_FOLLOW_RELATIONSHIPS)
                .where(
                    USER_FOLLOW_RELATIONSHIPS.FOLLOWER_ID.eq(userId),
                    USER_FOLLOW_RELATIONSHIPS.FOLLOWEE_ID.eq(USERS.ID)))
        .orderBy(USERS.CREATED_AT.desc())
        .limit(limit)
        .fetch(USERS.ID);
  }
}
