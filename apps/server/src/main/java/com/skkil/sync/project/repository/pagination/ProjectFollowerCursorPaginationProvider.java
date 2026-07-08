package com.skkil.sync.project.repository.pagination;

import static com.skkil.sync.jooq.tables.ProjectFollowRelationships.PROJECT_FOLLOW_RELATIONSHIPS;

import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import com.skkil.sync.project.dto.data.ProjectFollowerCursor;
import com.skkil.sync.project.dto.data.ProjectFollowerDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProjectFollowerCursorPaginationProvider
    extends KeysetCursorPaginationProvider<ProjectFollowerDto, ProjectFollowerCursor> {

  @Override
  public Class<ProjectFollowerCursor> getCursorClass() {
    return ProjectFollowerCursor.class;
  }

  @Override
  protected List<KeysetField<ProjectFollowerCursor, ?>> getKeysetFields() {
    return List.of(
        KeysetField.asc(PROJECT_FOLLOW_RELATIONSHIPS.ID, ProjectFollowerCursor::relationshipId));
  }

  @Override
  public ProjectFollowerCursor convert(ProjectFollowerDto entity) {
    return new ProjectFollowerCursor(entity.relationshipId());
  }
}
