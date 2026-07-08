package com.skkil.sync.project.repository;

import static com.skkil.sync.jooq.tables.ProjectFollowRelationships.PROJECT_FOLLOW_RELATIONSHIPS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.project.dto.data.ProjectFollowerDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectFollowerQueryRepository {

  private final DSLContext dsl;

  public ProjectFollowerQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<ProjectFollowerDto> getFollowers(Long projectId) {
    return (condition, orderFields, size) ->
        dsl.select(
                PROJECT_FOLLOW_RELATIONSHIPS.ID.as("relationshipId"),
                PROJECT_FOLLOW_RELATIONSHIPS.FOLLOWER_ID.as("userId"))
            .from(PROJECT_FOLLOW_RELATIONSHIPS)
            .where(condition.and(PROJECT_FOLLOW_RELATIONSHIPS.PROJECT_ID.eq(projectId)))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(ProjectFollowerDto.class);
  }
}
