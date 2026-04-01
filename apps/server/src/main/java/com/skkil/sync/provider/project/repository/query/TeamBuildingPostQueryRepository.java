package com.skkil.sync.provider.project.repository.query;

import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;
import static com.skkil.sync.jooq.tables.TeamBuildingPosts.TEAM_BUILDING_POSTS;

import com.skkil.sync.provider.project.dto.data.TeamBuildingPostDto;
import com.skkil.sync.provider.project.dto.query.TeamBuildingPostQuery;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class TeamBuildingPostQueryRepository {

  private final DSLContext dsl;

  public TeamBuildingPostQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<TeamBuildingPostDto> getTeamBuildingPosts(TeamBuildingPostQuery query, int size) {
    Condition condition = buildCursorCondition(query);

    return dsl.select(
            TEAM_BUILDING_POSTS.ID,
            TEAM_BUILDING_POSTS.PROJECT_ID,
            PROVIDERS.NAME,
            PROVIDERS.DESCRIPTION,
            TEAM_BUILDING_POSTS.TITLE,
            TEAM_BUILDING_POSTS.CONTENT,
            TEAM_BUILDING_POSTS.CREATED_AT,
            TEAM_BUILDING_POSTS.UPDATED_AT)
        .from(TEAM_BUILDING_POSTS)
        .join(PROVIDERS)
        .on(TEAM_BUILDING_POSTS.PROJECT_ID.eq(PROVIDERS.ID))
        .where(condition)
        .orderBy(TEAM_BUILDING_POSTS.CREATED_AT.asc(), TEAM_BUILDING_POSTS.ID.asc())
        .limit(size)
        .fetchInto(TeamBuildingPostDto.class);
  }

  private Condition buildCursorCondition(TeamBuildingPostQuery query) {
    Condition condition = PROVIDERS.VERIFICATION_STATUS.eq("VERIFIED");

    if (query.createdAfter() != null && query.afterId() != null) {
      LocalDateTime createdAt = LocalDateTime.ofInstant(query.createdAfter(), ZoneOffset.UTC);
      condition =
          condition.and(
              TEAM_BUILDING_POSTS
                  .CREATED_AT
                  .gt(createdAt)
                  .or(
                      TEAM_BUILDING_POSTS
                          .CREATED_AT
                          .eq(createdAt)
                          .and(TEAM_BUILDING_POSTS.ID.gt(query.afterId()))));
    } else if (query.createdAfter() != null) {
      condition =
          condition.and(
              TEAM_BUILDING_POSTS.CREATED_AT.gt(
                  LocalDateTime.ofInstant(query.createdAfter(), ZoneOffset.UTC)));
    } else if (query.afterId() != null) {
      condition = condition.and(TEAM_BUILDING_POSTS.ID.gt(query.afterId()));
    }

    if (query.projectId() != null) {
      condition = condition.and(TEAM_BUILDING_POSTS.PROJECT_ID.eq(query.projectId()));
    }

    return condition;
  }
}
