package com.skkil.sync.comment.repository;

import static com.skkil.sync.jooq.tables.Comments.COMMENTS;

import com.skkil.sync.comment.dto.data.CommentDto;
import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class CommentQueryRepository {

  private final DSLContext dsl;

  public CommentQueryRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public CursorPaginationDataFetcher<CommentDto> getCommentsByPost(Long postId) {
    return (condition, orderFields, size) ->
        dsl.select(
                COMMENTS.ID.as("id"),
                COMMENTS.AUTHOR_ID.as("authorId"),
                COMMENTS.CONTENT.as("content"),
                COMMENTS.DELETED_AT.isNotNull().as("deleted"),
                COMMENTS.IS_ACCEPTED.as("accepted"),
                COMMENTS.CREATED_AT.as("createdAt"),
                COMMENTS.UPDATED_AT.as("updatedAt"))
            .from(COMMENTS)
            .where(condition.and(COMMENTS.POST_ID.eq(postId)))
            .orderBy(orderFields)
            .limit(size)
            .fetchInto(CommentDto.class);
  }
}
