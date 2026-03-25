package com.skkil.sync.reflection.repository.query;

import static com.skkil.sync.jooq.tables.Experiences.EXPERIENCES;
import static com.skkil.sync.jooq.tables.ProjectExperiences.PROJECT_EXPERIENCES;
import static com.skkil.sync.jooq.tables.Reflections.REFLECTIONS;
import static com.skkil.sync.jooq.tables.Users.USERS;

import com.skkil.sync.common.util.pagination.model.Cursor;
import com.skkil.sync.reflection.dto.data.ReflectionDto;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class ReflectionQueryRepository {

  private final DSLContext dsl;

  public ReflectionQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<ReflectionDto> getReflections(Cursor cursor, int size) {
    Condition cursorCondition = buildCursorCondition(cursor);

    return dsl.select(
            REFLECTIONS.ID,
            REFLECTIONS.AUTHOR_ID,
            USERS.FULL_NAME,
            REFLECTIONS.CONTENT,
            REFLECTIONS.AUTHOR_ID,
            REFLECTIONS.PROJECT_EXPERIENCE_ID,
            REFLECTIONS.CREATED_AT,
            REFLECTIONS.UPDATED_AT)
        .from(REFLECTIONS)
        .join(USERS)
        .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
        .join(PROJECT_EXPERIENCES)
        .on(REFLECTIONS.PROJECT_EXPERIENCE_ID.eq(PROJECT_EXPERIENCES.ID))
        .join(EXPERIENCES)
        .on(PROJECT_EXPERIENCES.ID.eq(EXPERIENCES.ID))
        .where(cursorCondition)
        .orderBy(REFLECTIONS.CREATED_AT.desc(), REFLECTIONS.ID.desc())
        .limit(size)
        .fetchInto(ReflectionDto.class);
  }

  public List<ReflectionDto> getReflectionsByUser(Long userId, Cursor cursor, int size) {
    Condition cursorCondition = buildCursorCondition(cursor);

    return dsl.select(
            REFLECTIONS.ID,
            REFLECTIONS.AUTHOR_ID,
            USERS.FULL_NAME,
            REFLECTIONS.CONTENT,
            REFLECTIONS.AUTHOR_ID,
            REFLECTIONS.PROJECT_EXPERIENCE_ID,
            REFLECTIONS.CREATED_AT,
            REFLECTIONS.UPDATED_AT)
        .from(REFLECTIONS)
        .join(USERS)
        .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
        .join(PROJECT_EXPERIENCES)
        .on(REFLECTIONS.PROJECT_EXPERIENCE_ID.eq(PROJECT_EXPERIENCES.ID))
        .join(EXPERIENCES)
        .on(PROJECT_EXPERIENCES.ID.eq(EXPERIENCES.ID))
        .where(cursorCondition.and(REFLECTIONS.AUTHOR_ID.eq(userId)))
        .orderBy(REFLECTIONS.CREATED_AT.desc(), REFLECTIONS.ID.desc())
        .limit(size)
        .fetchInto(ReflectionDto.class);
  }

  public List<ReflectionDto> getReflectionsByExperience(
      Long experienceId, Cursor cursor, int size) {
    Condition cursorCondition = buildCursorCondition(cursor);

    return dsl.select(
            REFLECTIONS.ID,
            REFLECTIONS.AUTHOR_ID,
            USERS.FULL_NAME,
            REFLECTIONS.CONTENT,
            REFLECTIONS.AUTHOR_ID,
            REFLECTIONS.PROJECT_EXPERIENCE_ID,
            REFLECTIONS.CREATED_AT,
            REFLECTIONS.UPDATED_AT)
        .from(REFLECTIONS)
        .join(USERS)
        .on(REFLECTIONS.AUTHOR_ID.eq(USERS.ID))
        .join(PROJECT_EXPERIENCES)
        .on(REFLECTIONS.PROJECT_EXPERIENCE_ID.eq(PROJECT_EXPERIENCES.ID))
        .join(EXPERIENCES)
        .on(PROJECT_EXPERIENCES.ID.eq(EXPERIENCES.ID))
        .where(cursorCondition.and(REFLECTIONS.PROJECT_EXPERIENCE_ID.eq(PROJECT_EXPERIENCES.ID)))
        .orderBy(REFLECTIONS.CREATED_AT.desc(), REFLECTIONS.ID.desc())
        .limit(size)
        .fetchInto(ReflectionDto.class);
  }

  private Condition buildCursorCondition(Cursor cursor) {
    if (cursor == null) {
      return null;
    }

    LocalDateTime createdAt = LocalDateTime.ofInstant(cursor.createdAt(), ZoneOffset.UTC);

    return REFLECTIONS
        .CREATED_AT
        .lt(createdAt)
        .or(REFLECTIONS.CREATED_AT.eq(createdAt).and(REFLECTIONS.ID.lt(cursor.id())));
  }
}
