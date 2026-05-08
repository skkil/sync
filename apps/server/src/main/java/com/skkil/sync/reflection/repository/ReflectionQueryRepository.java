package com.skkil.sync.reflection.repository;

import static com.skkil.sync.jooq.tables.Experiences.EXPERIENCES;
import static com.skkil.sync.jooq.tables.ProjectExperiences.PROJECT_EXPERIENCES;
import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;
import static com.skkil.sync.jooq.tables.Reflections.REFLECTIONS;
import static com.skkil.sync.jooq.tables.Users.USERS;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.reflection.dto.data.ReflectionDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class ReflectionQueryRepository {

  private final DSLContext dsl;

  public ReflectionQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<ReflectionDto> getReflections() {
    return (condition, orderFields, size) ->
        dsl.select(
                REFLECTIONS.ID,
                REFLECTIONS.AUTHOR_ID,
                USERS.FULL_NAME,
                REFLECTIONS.CONTENT,
                PROVIDERS.ID,
                PROVIDERS.NAME,
                field(name("reflections", "like_count"), Long.class),
                field(name("reflections", "comment_count"), Long.class),
                REFLECTIONS.CREATED_AT,
                REFLECTIONS.UPDATED_AT)
            .from(REFLECTIONS)
            .join(USERS)
            .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
            .leftJoin(PROJECT_EXPERIENCES)
            .on(REFLECTIONS.PROJECT_EXPERIENCE_ID.eq(PROJECT_EXPERIENCES.ID))
            .leftJoin(EXPERIENCES)
            .on(PROJECT_EXPERIENCES.ID.eq(EXPERIENCES.ID))
            .leftJoin(PROVIDERS)
            .on(EXPERIENCES.PROVIDER_ID.eq(PROVIDERS.ID))
            .where(condition)
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(ReflectionDto.class);
  }

  public CursorPaginationDataFetcher<ReflectionDto> getReflectionsByUser(Long userId) {
    return (condition, orderFields, size) -> {
      CursorPaginationDataFetcher<ReflectionDto> base = getReflections();
      return base.fetch(condition.and(REFLECTIONS.AUTHOR_ID.eq(userId)), orderFields, size);
    };
  }

  public CursorPaginationDataFetcher<ReflectionDto> getReflectionsByExperience(Long experienceId) {
    return (condition, orderFields, size) -> {
      CursorPaginationDataFetcher<ReflectionDto> base = getReflections();
      return base.fetch(
          condition.and(REFLECTIONS.PROJECT_EXPERIENCE_ID.eq(experienceId)), orderFields, size);
    };
  }
}
