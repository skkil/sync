package com.skkil.sync.provider.project.repository.pagination;

import static com.skkil.sync.jooq.tables.TeamBuildingPosts.TEAM_BUILDING_POSTS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import com.skkil.sync.provider.project.dto.data.TeamBuildingPostCursor;
import com.skkil.sync.provider.project.dto.data.TeamBuildingPostDto;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.stereotype.Component;

@Component
public class TeamBuildingPostCursorPaginationProvider
    implements CursorPaginationProvider<TeamBuildingPostDto, TeamBuildingPostCursor> {

  @Override
  public Class<TeamBuildingPostCursor> getCursorClass() {
    return TeamBuildingPostCursor.class;
  }

  @Override
  public Condition getNextCondition(TeamBuildingPostCursor cursor) {
    return TEAM_BUILDING_POSTS.ID.gt(cursor.id());
  }

  @Override
  public Condition getPreviousCondition(TeamBuildingPostCursor cursor) {
    return TEAM_BUILDING_POSTS.ID.lt(cursor.id());
  }

  @Override
  public List<OrderField<?>> getOrderFields() {
    return List.of(TEAM_BUILDING_POSTS.ID.asc());
  }

  @Override
  public TeamBuildingPostCursor convert(TeamBuildingPostDto entity) {
    return new TeamBuildingPostCursor(entity.id());
  }
}
