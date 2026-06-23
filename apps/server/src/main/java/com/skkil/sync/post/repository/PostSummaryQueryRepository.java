package com.skkil.sync.post.repository;

import static com.skkil.sync.jooq.tables.PostSummaries.POST_SUMMARIES;
import static com.skkil.sync.jooq.tables.Posts.POSTS;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.post.dto.data.SummaryDto;
import com.skkil.sync.post.model.PostVisibility;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class PostSummaryQueryRepository {

  private final DSLContext dsl;

  public PostSummaryQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<SummaryDto> getSummaries(Long authorId) {
    return (condition, orderFields, size) ->
        dsl.select(
                POSTS.ID.as("postId"),
                POSTS.SLUG.as("slug"),
                POSTS.TITLE.as("title"),
                DSL.coalesce(POST_SUMMARIES.SUMMARY, POSTS.CONTENT).as("displayText"),
                POSTS.CREATED_AT.as("createdAt"))
            .from(POSTS)
            .leftJoin(POST_SUMMARIES)
            .on(POSTS.ID.eq(POST_SUMMARIES.ID))
            .where(
                condition
                    .and(POSTS.AUTHOR_ID.eq(authorId))
                    .and(POSTS.VISIBILITY.eq(PostVisibility.VISIBLE.name())))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(SummaryDto.class);
  }
}
