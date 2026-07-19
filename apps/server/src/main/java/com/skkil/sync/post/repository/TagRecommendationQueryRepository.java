package com.skkil.sync.post.repository;

import static com.skkil.sync.jooq.tables.PostTags.POST_TAGS;
import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static com.skkil.sync.jooq.tables.TagFollowRelationships.TAG_FOLLOW_RELATIONSHIPS;
import static com.skkil.sync.jooq.tables.Tags.TAGS;

import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostVisibility;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Select;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class TagRecommendationQueryRepository {

  private static final int TRENDING_WINDOW_DAYS = 7;

  private final DSLContext dsl;

  public TagRecommendationQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  // 최근 7일간 게시글에 많이 사용된 전역 태그를 사용 횟수가 많은 순으로 추천한다.
  public List<Long> findTrendingCandidateIds(Long userId, int limit) {
    OffsetDateTime since = OffsetDateTime.now(ZoneOffset.UTC).minusDays(TRENDING_WINDOW_DAYS);

    return dsl.select(POST_TAGS.TAG_ID)
        .from(POST_TAGS)
        .join(POSTS)
        .on(POSTS.ID.eq(POST_TAGS.POST_ID))
        .where(POSTS.CREATED_AT.ge(since))
        .and(visibleCondition())
        .and(publicPublishedCondition())
        .and(isGlobalVerifiedTag(POST_TAGS.TAG_ID))
        .andNotExists(alreadyFollowing(userId, POST_TAGS.TAG_ID))
        .groupBy(POST_TAGS.TAG_ID)
        .orderBy(DSL.count().desc())
        .limit(limit)
        .fetch(POST_TAGS.TAG_ID);
  }

  // 신호 기반 후보만으로 추천 태그가 부족할 때(콜드 스타트) 채워 넣을 대체 후보 목록
  public List<Long> findRecentlyCreatedCandidateIds(Long userId, int limit) {
    return dsl.select(TAGS.ID)
        .from(TAGS)
        .where(TAGS.PROJECT_ID.isNull(), TAGS.VERIFIED.isTrue())
        .andNotExists(alreadyFollowing(userId, TAGS.ID))
        .orderBy(TAGS.CREATED_AT.desc())
        .limit(limit)
        .fetch(TAGS.ID);
  }

  private Condition isGlobalVerifiedTag(Field<Long> tagId) {
    return DSL.exists(
        dsl.selectOne()
            .from(TAGS)
            .where(TAGS.ID.eq(tagId), TAGS.PROJECT_ID.isNull(), TAGS.VERIFIED.isTrue()));
  }

  private Select<?> alreadyFollowing(Long userId, Field<Long> tagId) {
    return dsl.selectOne()
        .from(TAG_FOLLOW_RELATIONSHIPS)
        .where(
            TAG_FOLLOW_RELATIONSHIPS.FOLLOWER_ID.eq(userId),
            TAG_FOLLOW_RELATIONSHIPS.TAG_ID.eq(tagId));
  }

  private Condition visibleCondition() {
    return POSTS.VISIBILITY.eq(PostVisibility.VISIBLE.name());
  }

  private Condition publicPublishedCondition() {
    return POSTS.PROJECT_ID.isNull().and(POSTS.STATUS.eq(PostStatus.PUBLISHED.name()));
  }
}
