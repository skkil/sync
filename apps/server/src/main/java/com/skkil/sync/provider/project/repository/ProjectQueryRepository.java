package com.skkil.sync.provider.project.repository;

import static com.skkil.sync.jooq.tables.Projects.PROJECTS;
import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.provider.project.dto.data.ProjectDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectQueryRepository {

  private final DSLContext dsl;

  public ProjectQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<ProjectDto> getTrendingProjects() {
    return (condition, orderFields, size) ->
        dsl.select(
                PROJECTS.ID,
                PROVIDERS.NAME,
                PROVIDERS.DESCRIPTION,
                PROVIDERS.CREATED_AT,
                PROVIDERS.UPDATED_AT)
            .from(PROJECTS)
            .join(PROVIDERS)
            .on(PROJECTS.ID.eq(PROVIDERS.ID))
            .where(condition)
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(ProjectDto.class);
  }
}
