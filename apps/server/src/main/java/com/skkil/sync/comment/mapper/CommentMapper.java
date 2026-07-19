package com.skkil.sync.comment.mapper;

import com.skkil.sync.comment.dto.data.CommentDto;
import com.skkil.sync.comment.dto.summary.CommentSummary;
import com.skkil.sync.user.dto.summary.UserSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  default CommentSummary toCommentSummary(
      CommentDto comment, UserSummary author, boolean isPostAuthor) {
    return CommentSummary.builder()
        .id(comment.id())
        .author(author)
        .isPostAuthor(isPostAuthor)
        .content(comment.content())
        .isDeleted(Boolean.TRUE.equals(comment.deleted()))
        .isAccepted(Boolean.TRUE.equals(comment.accepted()))
        .createdAt(comment.createdAt())
        .updatedAt(comment.updatedAt())
        .build();
  }
}
