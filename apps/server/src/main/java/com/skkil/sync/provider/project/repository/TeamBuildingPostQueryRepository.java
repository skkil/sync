package com.skkil.sync.provider.project.repository;

import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;
import static com.skkil.sync.jooq.tables.TeamBuildingPosts.TEAM_BUILDING_POSTS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.provider.project.dto.data.TeamBuildingPostDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class TeamBuildingPostQueryRepository {

  private final DSLContext dsl;

  public TeamBuildingPostQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<TeamBuildingPostDto> getTeamBuildingPosts() {
    return (condition, orderFields, size) ->
        dsl.select(
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
            .where(condition.and(PROVIDERS.VERIFICATION_STATUS.eq("VERIFIED")))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(TeamBuildingPostDto.class);
  }

  public CursorPaginationDataFetcher<TeamBuildingPostDto> getTeamBuildingPostsByProject(
      Long projectId) {
    return (condition, orderFields, size) -> {
      var base = getTeamBuildingPosts();
      return base.fetch(
          condition.and(TEAM_BUILDING_POSTS.PROJECT_ID.eq(projectId)), orderFields, size);
    };
  }
}
