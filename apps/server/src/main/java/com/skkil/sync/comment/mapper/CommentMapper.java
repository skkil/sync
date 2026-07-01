package com.skkil.sync.comment.mapper;

import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.post.model.Post;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  default GetCommentsResponse toGetCommentsResponse(
      Post post, List<Comment> comments, Map<Long, URL> profileImageUrls) {
    return new GetCommentsResponse(
        comments.stream()
            .map(comment -> toGetCommentsResponseComment(post, comment, profileImageUrls))
            .toList());
  }

  default GetCommentsResponse.Comment toGetCommentsResponseComment(
      Post post, Comment comment, Map<Long, URL> profileImageUrls) {
    var profileImage = comment.getAuthor().getProfileImage();
    var url = profileImage != null ? profileImageUrls.get(profileImage.getId()) : null;

    GetCommentsResponse.Author author =
        GetCommentsResponse.Author.builder()
            .id(comment.getAuthor().getId())
            .handle(comment.getAuthor().getHandle())
            .name(comment.getAuthor().getFullName())
            .profileImageUrl(url != null ? url.toString() : null)
            .isPostAuthor(comment.getAuthor().equals(post.getAuthor()))
            .build();

    return new GetCommentsResponse.Comment(
        comment.getId(),
        author,
        comment.getContent(),
        comment.isDeleted(),
        comment.getCreatedAt(),
        comment.getUpdatedAt());
  }
}
