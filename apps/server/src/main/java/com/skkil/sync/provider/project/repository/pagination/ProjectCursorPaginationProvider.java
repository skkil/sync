package com.skkil.sync.provider.project.repository.pagination;

import static com.skkil.sync.jooq.tables.Projects.PROJECTS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import com.skkil.sync.provider.project.dto.data.ProjectCursor;
import com.skkil.sync.provider.project.dto.data.ProjectDto;
import java.util.List;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.stereotype.Component;

@Component
public class ProjectCursorPaginationProvider
    implements CursorPaginationProvider<ProjectDto, ProjectCursor> {

  @Override
  public Class<ProjectCursor> getCursorClass() {
    return ProjectCursor.class;
  }

  @Override
  public Condition getNextCondition(ProjectCursor cursor) {
    return PROJECTS.ID.gt(cursor.id());
  }

  @Override
  public Condition getPreviousCondition(ProjectCursor cursor) {
    return PROJECTS.ID.lt(cursor.id());
  }

  @Override
  public List<OrderField<?>> getOrderFields() {
    return List.of(PROJECTS.ID.asc());
  }

  @Override
  public List<OrderField<?>> getReversedOrderFields() {
    return List.of(PROJECTS.ID.desc());
  }

  @Override
  public ProjectCursor convert(ProjectDto entity) {
    return new ProjectCursor(entity.id());
  }
}
