package com.skkil.sync.post.repository;

import static com.skkil.sync.jooq.tables.Posts.POSTS;
import static com.skkil.sync.jooq.tables.ProjectFollowRelationships.PROJECT_FOLLOW_RELATIONSHIPS;
import static com.skkil.sync.jooq.tables.UserFollowRelationships.USER_FOLLOW_RELATIONSHIPS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.post.dto.data.PostRecommendationCandidate;
import com.skkil.sync.post.model.PostScope;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostVisibility;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class PostRecommendationQueryRepository {

  private static final int TRENDING_WINDOW_DAYS = 7;

  private final DSLContext dsl;

  public PostRecommendationQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  /**
   * 추천 후보 게시글의 ID와 정렬에 필요한 최소 정보만 조회한다. 실제 게시글 데이터는 {@link
   * com.skkil.sync.post.repository.PostQueryRepository#getPostsByIds}로 materialize한다. 정렬 순서는 호출 측에서
   * 전달하는 {@code orderFields}(채널별 {@link
   * com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider})에 의해 결정된다.
   */
  public CursorPaginationDataFetcher<PostRecommendationCandidate> getCandidates(
      Condition channelCondition) {
    return (condition, orderFields, size) ->
        dsl.select(
                POSTS.ID.as("id"),
                POSTS.CREATED_AT.as("createdAt"),
                POSTS.LIKE_COUNT.as("likeCount"))
            .from(POSTS)
            .where(
                condition
                    .and(visibleCondition())
                    .and(publicPublishedCondition())
                    .and(channelCondition))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(PostRecommendationCandidate.class);
  }

  public Condition followingCondition(Long requesterId) {
    return DSL.exists(
            dsl.selectOne()
                .from(USER_FOLLOW_RELATIONSHIPS)
                .where(
                    USER_FOLLOW_RELATIONSHIPS.FOLLOWER_ID.eq(requesterId),
                    USER_FOLLOW_RELATIONSHIPS.FOLLOWEE_ID.eq(POSTS.AUTHOR_ID)))
        .or(
            DSL.exists(
                dsl.selectOne()
                    .from(PROJECT_FOLLOW_RELATIONSHIPS)
                    .where(
                        PROJECT_FOLLOW_RELATIONSHIPS.FOLLOWER_ID.eq(requesterId),
                        PROJECT_FOLLOW_RELATIONSHIPS.PROJECT_ID.eq(POSTS.PROJECT_ID))));
  }

  public Condition trendingCondition() {
    OffsetDateTime since = OffsetDateTime.now(ZoneOffset.UTC).minusDays(TRENDING_WINDOW_DAYS);
    return POSTS.CREATED_AT.ge(since);
  }

  private Condition visibleCondition() {
    return POSTS.VISIBILITY.eq(PostVisibility.VISIBLE.name());
  }

  private Condition publicPublishedCondition() {
    return POSTS
        .SCOPE
        .eq(PostScope.PUBLIC.name())
        .and(POSTS.STATUS.eq(PostStatus.PUBLISHED.name()));
  }
}
