package com.skkil.sync.provider.project.repository.query;

import static com.skkil.sync.jooq.tables.Projects.PROJECTS;
import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;

import com.skkil.sync.provider.project.dto.data.ProjectDto;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectQueryRepository {

  private final DSLContext dsl;

  public ProjectQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<ProjectDto> getTrendingProjects(Instant createdAfter, Long afterId, int size) {
    Condition condition = buildCursorCondition(createdAfter, afterId);

    return dsl.select(
            PROJECTS.ID,
            PROVIDERS.NAME,
            PROVIDERS.DESCRIPTION,
            PROVIDERS.CREATED_AT,
            PROVIDERS.UPDATED_AT)
        .from(PROJECTS)
        .join(PROVIDERS)
        .on(PROJECTS.ID.eq(PROVIDERS.ID))
        .where(condition)
        .orderBy(PROVIDERS.CREATED_AT.desc(), PROJECTS.ID.desc())
        .limit(size)
        .fetchInto(ProjectDto.class);
  }

  private Condition buildCursorCondition(Instant createdAfter, Long afterId) {
    Condition condition = PROVIDERS.VERIFICATION_STATUS.eq("VERIFIED");

    if (createdAfter != null && afterId != null) {
      LocalDateTime createdAt = LocalDateTime.ofInstant(createdAfter, ZoneOffset.UTC);
      condition =
          condition.and(
              PROVIDERS
                  .CREATED_AT
                  .lt(createdAt)
                  .or(PROVIDERS.CREATED_AT.eq(createdAt).and(PROJECTS.ID.lt(afterId))));
    } else if (createdAfter != null) {
      condition =
          condition.and(
              PROVIDERS.CREATED_AT.lt(LocalDateTime.ofInstant(createdAfter, ZoneOffset.UTC)));
    } else if (afterId != null) {
      condition = condition.and(PROJECTS.ID.lt(afterId));
    }

    return condition;
  }
}
