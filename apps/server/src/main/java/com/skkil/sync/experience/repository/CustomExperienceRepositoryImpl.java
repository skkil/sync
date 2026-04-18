package com.skkil.sync.experience.repository;

import static com.skkil.sync.jooq.tables.Experiences.EXPERIENCES;
import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;

import com.skkil.sync.experience.dto.data.ProjectExperienceDto;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
class CustomExperienceRepositoryImpl implements CustomExperienceRepository {

  private final DSLContext dsl;

  public CustomExperienceRepositoryImpl(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public List<ProjectExperienceDto> findProjectExperiencesByUserId(Long userId) {
    return dsl.select(EXPERIENCES.ID, PROVIDERS.ID, PROVIDERS.NAME)
        .from(EXPERIENCES)
        .join(PROVIDERS)
        .on(EXPERIENCES.PROVIDER_ID.eq(PROVIDERS.ID))
        .where(EXPERIENCES.USER_ID.eq(userId))
        .fetchInto(ProjectExperienceDto.class);
  }
}
