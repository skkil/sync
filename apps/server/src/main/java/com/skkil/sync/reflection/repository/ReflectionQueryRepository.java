package com.skkil.sync.reflection.repository;

import static com.skkil.sync.jooq.tables.Projects.PROJECTS;
import static com.skkil.sync.jooq.tables.ReflectionBookmarks.REFLECTION_BOOKMARKS;
import static com.skkil.sync.jooq.tables.Reflections.REFLECTIONS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.reflection.dto.data.ReflectionDto;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class ReflectionQueryRepository {

  private final DSLContext dsl;

  public ReflectionQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public Optional<ReflectionDto> getReflectionBySlug(Long requesterId, String slug) {
    return dsl.select(
            REFLECTIONS.ID.as("id"),
            REFLECTIONS.SLUG.as("slug"),
            REFLECTIONS.AUTHOR_ID.as("authorId"),
            USERS.FULL_NAME.as("authorName"),
            PROJECTS.ID.as("projectId"),
            PROJECTS.NAME.as("projectName"),
            REFLECTIONS.CONTENT.as("content"),
            REFLECTIONS.CREATED_AT.as("createdAt"),
            REFLECTIONS.UPDATED_AT.as("updatedAt"),
            DSL.value(0L).as("likeCount"),
            DSL.value(0L).as("commentCount"),
            bookmarkedField(requesterId))
        .from(REFLECTIONS)
        .join(USERS)
        .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
        .join(PROJECTS)
        .on(REFLECTIONS.PROJECT_ID.eq(PROJECTS.ID))
        .where(REFLECTIONS.SLUG.eq(slug))
        .fetchOptional()
        .map(record -> record.into(ReflectionDto.class));
  }

  public CursorPaginationDataFetcher<ReflectionDto> getReflections() {
    return (condition, orderFields, size) ->
        dsl.select(
                REFLECTIONS.ID.as("id"),
                REFLECTIONS.SLUG.as("slug"),
                REFLECTIONS.AUTHOR_ID.as("authorId"),
                USERS.FULL_NAME.as("authorName"),
                PROJECTS.ID.as("projectId"),
                PROJECTS.NAME.as("projectName"),
                REFLECTIONS.CONTENT.as("content"),
                REFLECTIONS.CREATED_AT.as("createdAt"),
                REFLECTIONS.UPDATED_AT.as("updatedAt"),
                DSL.value(0L).as("likeCount"),
                DSL.value(0L).as("commentCount"),
                bookmarkedField(null))
            .from(REFLECTIONS)
            .join(USERS)
            .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
            .join(PROJECTS)
            .on(REFLECTIONS.PROJECT_ID.eq(PROJECTS.ID))
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

  public CursorPaginationDataFetcher<ReflectionDto> getReflectionsByProject(String handle) {
    return (condition, orderFields, size) -> {
      CursorPaginationDataFetcher<ReflectionDto> base = getReflections();
      return base.fetch(condition.and(PROJECTS.HANDLE.eq(handle)), orderFields, size);
    };
  }

  public List<ReflectionDto> getReflectionsByIds(List<Long> ids) {
    if (ids.isEmpty()) {
      return List.of();
    }

    Map<Long, ReflectionDto> byId =
        dsl
            .select(
                REFLECTIONS.ID.as("id"),
                REFLECTIONS.SLUG.as("slug"),
                REFLECTIONS.AUTHOR_ID.as("authorId"),
                USERS.FULL_NAME.as("authorName"),
                PROJECTS.ID.as("projectId"),
                PROJECTS.NAME.as("projectName"),
                REFLECTIONS.CONTENT.as("content"),
                REFLECTIONS.CREATED_AT.as("createdAt"),
                REFLECTIONS.UPDATED_AT.as("updatedAt"),
                DSL.value(0L).as("likeCount"),
                DSL.value(0L).as("commentCount"),
                bookmarkedField(null))
            .from(REFLECTIONS)
            .join(USERS)
            .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
            .join(PROJECTS)
            .on(REFLECTIONS.PROJECT_ID.eq(PROJECTS.ID))
            .where(REFLECTIONS.ID.in(ids))
            .fetchInto(ReflectionDto.class)
            .stream()
            .collect(Collectors.toMap(ReflectionDto::id, Function.identity()));

    return ids.stream().map(byId::get).filter(dto -> dto != null).toList();
  }

  private Field<Boolean> bookmarkedField(Long requesterId) {
    if (requesterId == null) {
      return DSL.value(false).as("bookmarked");
    }

    return DSL.field(
            DSL.exists(
                DSL.selectOne()
                    .from(REFLECTION_BOOKMARKS)
                    .where(REFLECTION_BOOKMARKS.REFLECTION_ID.eq(REFLECTIONS.ID))
                    .and(REFLECTION_BOOKMARKS.USER_ID.eq(requesterId))))
        .as("bookmarked");
  }
}
