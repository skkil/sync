package com.skkil.sync.comment.mapper;

import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.post.model.Post;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  default GetCommentsResponse toGetCommentsResponse(
      Post post,
      List<Comment> comments,
      Map<Long, List<Comment>> replies,
      Map<Long, String> profileImageUrls) {
    return new GetCommentsResponse(
        comments.stream()
            .map(comment -> toGetCommentsResponseComment(post, comment, replies, profileImageUrls))
            .toList());
  }

  default GetCommentsResponse.Comment toGetCommentsResponseComment(
      Post post,
      Comment comment,
      Map<Long, List<Comment>> replies,
      Map<Long, String> profileImageUrls) {
    String profileImage = profileImageUrls.get(comment.getAuthor().getId());

    GetCommentsResponse.Author author =
        GetCommentsResponse.Author.builder()
            .id(comment.getAuthor().getId())
            .handle(comment.getAuthor().getHandle())
            .name(comment.getAuthor().getFullName())
            .profileImageUrl(profileImage)
            .isPostAuthor(comment.getAuthor().equals(post.getAuthor()))
            .build();

    List<GetCommentsResponse.Comment> commentReplies =
        replies.getOrDefault(comment.getId(), List.of()).stream()
            .map(reply -> toGetCommentsResponseComment(post, reply, replies, profileImageUrls))
            .toList();

    return new GetCommentsResponse.Comment(
        comment.getId(),
        author,
        comment.getContent(),
        comment.isDeleted(),
        comment.getCreatedAt(),
        comment.getUpdatedAt(),
        commentReplies);
  }
}
