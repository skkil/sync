package com.skkil.sync.project.repository;

import static com.skkil.sync.jooq.tables.ProjectFollowRelationships.PROJECT_FOLLOW_RELATIONSHIPS;
import static com.skkil.sync.jooq.tables.Projects.PROJECTS;
import static com.skkil.sync.jooq.tables.Teammates.TEAMMATES;

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
public class ProjectRecommendationQueryRepository {

  private final DSLContext dsl;

  public ProjectRecommendationQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  // 최근 30일간 새로운 팔로워가 많이 늘어난 공개 프로젝트를 추천한다.
  public List<Long> findTrendingCandidateIds(Long userId, int limit) {
    OffsetDateTime since = OffsetDateTime.now(ZoneOffset.UTC).minusDays(30);
    var candidate = PROJECT_FOLLOW_RELATIONSHIPS.as("candidate");

    return dsl.select(candidate.PROJECT_ID)
        .from(candidate)
        .where(candidate.CREATED_AT.ge(since))
        .and(isPublicProject(candidate.PROJECT_ID))
        .andNotExists(alreadyFollowing(userId, candidate.PROJECT_ID))
        .andNotExists(isTeammate(userId, candidate.PROJECT_ID))
        .groupBy(candidate.PROJECT_ID)
        .orderBy(DSL.count().desc())
        .limit(limit)
        .fetch(candidate.PROJECT_ID);
  }

  private Condition isPublicProject(Field<Long> projectId) {
    return DSL.exists(
        dsl.selectOne()
            .from(PROJECTS)
            .where(PROJECTS.ID.eq(projectId), PROJECTS.IS_PUBLIC.isTrue()));
  }

  private Select<?> alreadyFollowing(Long userId, Field<Long> projectId) {
    return dsl.selectOne()
        .from(PROJECT_FOLLOW_RELATIONSHIPS)
        .where(
            PROJECT_FOLLOW_RELATIONSHIPS.FOLLOWER_ID.eq(userId),
            PROJECT_FOLLOW_RELATIONSHIPS.PROJECT_ID.eq(projectId));
  }

  private Select<?> isTeammate(Long userId, Field<Long> projectId) {
    return dsl.selectOne()
        .from(TEAMMATES)
        .where(TEAMMATES.USER_ID.eq(userId), TEAMMATES.PROJECT_ID.eq(projectId));
  }
}
