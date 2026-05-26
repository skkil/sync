package com.skkil.sync.reflection.repository;

import static com.skkil.sync.jooq.tables.ReflectionSummaries.REFLECTION_SUMMARIES;
import static com.skkil.sync.jooq.tables.Reflections.REFLECTIONS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.reflection.dto.data.SummaryDto;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class ReflectionSummaryQueryRepository {

  private final DSLContext dsl;

  public ReflectionSummaryQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<SummaryDto> getSummaries(Long authorId) {
    return (condition, orderFields, size) ->
        dsl.select(
                REFLECTIONS.ID.as("reflectionId"),
                REFLECTIONS.SLUG.as("slug"),
                REFLECTIONS.TITLE.as("title"),
                DSL.coalesce(REFLECTION_SUMMARIES.SUMMARY, REFLECTIONS.CONTENT).as("displayText"),
                REFLECTIONS.CREATED_AT.as("createdAt"))
            .from(REFLECTIONS)
            .leftJoin(REFLECTION_SUMMARIES)
            .on(REFLECTIONS.ID.eq(REFLECTION_SUMMARIES.ID))
            .where(condition.and(REFLECTIONS.AUTHOR_ID.eq(authorId)))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(SummaryDto.class);
  }
}
