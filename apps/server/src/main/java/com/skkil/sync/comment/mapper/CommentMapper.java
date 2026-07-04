package com.skkil.sync.comment.mapper;

import com.skkil.sync.comment.dto.data.CommentDto;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import java.net.URL;
import java.util.Map;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  default GetCommentsResponse toGetCommentsResponse(
      CursorPaginationResponse<CommentDto> comments,
      Long postAuthorId,
      Map<Long, URL> profileImageUrls) {
    return new GetCommentsResponse(
        comments.map(
            comment -> toGetCommentsResponseComment(comment, postAuthorId, profileImageUrls)));
  }

  default GetCommentsResponse.Comment toGetCommentsResponseComment(
      CommentDto comment, Long postAuthorId, Map<Long, URL> profileImageUrls) {
    URL profileImageUrl =
        comment.authorProfileImageId() == null
            ? null
            : profileImageUrls.get(comment.authorProfileImageId());

    GetCommentsResponse.Author author =
        GetCommentsResponse.Author.builder()
            .id(comment.authorId())
            .handle(comment.authorHandle())
            .name(comment.authorName())
            .profileImageUrl(profileImageUrl == null ? null : profileImageUrl.toExternalForm())
            .isPostAuthor(comment.authorId().equals(postAuthorId))
            .build();

    return new GetCommentsResponse.Comment(
        comment.id(),
        author,
        comment.content(),
        Boolean.TRUE.equals(comment.deleted()),
        comment.createdAt(),
        comment.updatedAt());
  }
}
