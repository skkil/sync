package com.skkil.sync.comment.mapper;

import com.skkil.sync.comment.dto.data.CommentDto;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.user.mapper.UserAssembler;
import org.springframework.stereotype.Component;

@Component
public class CommentAssembler {

  private final CommentMapper commentMapper;

  private final UserAssembler userAssembler;

  public CommentAssembler(CommentMapper commentMapper, UserAssembler userAssembler) {
    this.commentMapper = commentMapper;
    this.userAssembler = userAssembler;
  }

  public GetCommentsResponse toGetCommentsResponse(
      CursorPaginationResponse<CommentDto> comments, Long postAuthorId) {
    return new GetCommentsResponse(
        comments.mapWithLookup(
            CommentDto::authorId,
            userAssembler::toUserSummaries,
            (comment, authorSummaries) ->
                commentMapper.toCommentSummary(
                    comment,
                    authorSummaries.get(comment.authorId()),
                    comment.authorId().equals(postAuthorId))));
  }
}
