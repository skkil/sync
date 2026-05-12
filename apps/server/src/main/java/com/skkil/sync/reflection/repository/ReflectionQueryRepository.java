package com.skkil.sync.reflection.repository;

import static com.skkil.sync.jooq.tables.Experiences.EXPERIENCES;
import static com.skkil.sync.jooq.tables.ProjectExperiences.PROJECT_EXPERIENCES;
import static com.skkil.sync.jooq.tables.Providers.PROVIDERS;
import static com.skkil.sync.jooq.tables.Reflections.REFLECTIONS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.reflection.dto.data.ReflectionDto;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class ReflectionQueryRepository {

  private final DSLContext dsl;

  public ReflectionQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public Optional<ReflectionDto> getReflectionBySlug(String slug) {
    return dsl.select(
            REFLECTIONS.ID.as("id"),
            REFLECTIONS.AUTHOR_ID.as("authorId"),
            USERS.FULL_NAME.as("authorName"),
            REFLECTIONS.CONTENT.as("content"),
            PROVIDERS.ID.as("projectId"),
            PROVIDERS.NAME.as("projectName"),
            REFLECTIONS.CREATED_AT.as("createdAt"),
            REFLECTIONS.UPDATED_AT.as("updatedAt"),
            DSL.value(0L).as("likeCount"),
            DSL.value(0L).as("commentCount"))
        .from(REFLECTIONS)
        .join(USERS)
        .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
        .leftJoin(PROJECT_EXPERIENCES)
        .on(REFLECTIONS.PROJECT_EXPERIENCE_ID.eq(PROJECT_EXPERIENCES.ID))
        .leftJoin(EXPERIENCES)
        .on(PROJECT_EXPERIENCES.ID.eq(EXPERIENCES.ID))
        .leftJoin(PROVIDERS)
        .on(EXPERIENCES.PROVIDER_ID.eq(PROVIDERS.ID))
        .where(REFLECTIONS.SLUG.eq(slug))
        .fetchOptional()
        .map(record -> record.into(ReflectionDto.class));
  }

  public CursorPaginationDataFetcher<ReflectionDto> getReflections() {
    return (condition, orderFields, size) ->
        dsl.select(
                REFLECTIONS.ID.as("id"),
                REFLECTIONS.AUTHOR_ID.as("authorId"),
                USERS.FULL_NAME.as("authorName"),
                REFLECTIONS.CONTENT.as("content"),
                PROVIDERS.ID.as("projectId"),
                PROVIDERS.NAME.as("projectName"),
                REFLECTIONS.CREATED_AT.as("createdAt"),
                REFLECTIONS.UPDATED_AT.as("updatedAt"),
                DSL.value(0L).as("likeCount"),
                DSL.value(0L).as("commentCount"))
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
