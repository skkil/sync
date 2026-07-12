package com.skkil.sync.post.mapper;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.media.dto.MediaDto;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.dto.summary.PostSummary;
import com.skkil.sync.user.dto.summary.UserSummary;
import com.skkil.sync.user.mapper.UserAssembler;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PostAssembler {

  private final PostMapper postMapper;

  private final UserAssembler userAssembler;

  public PostAssembler(PostMapper postMapper, UserAssembler userAssembler) {
    this.postMapper = postMapper;
    this.userAssembler = userAssembler;
  }

  public GetPostResponse toGetPostResponse(PostDto post, List<MediaDto> media, Long requesterId) {
    UserSummary author = userAssembler.toUserSummary(post.authorId());

    return GetPostResponse.builder()
        .summary(toPostSummary(post, author, requesterId))
        .content(postMapper.toContent(post, media))
        .build();
  }

  public GetPostsResponse.Post toPostResponse(PostDto post, UserSummary author, Long requesterId) {
    return GetPostsResponse.Post.builder()
        .summary(toPostSummary(post, author, requesterId))
        .content(post.content())
        .build();
  }

  public GetPostsResponse.Post toPostResponse(PostDto post, UserSummary author) {
    return toPostResponse(post, author, null);
  }

  public CursorPaginationResponse<GetPostsResponse.Post> toPostResponses(
      CursorPaginationResponse<PostDto> posts, Long requesterId) {
    return posts.mapWithLookup(
        PostDto::authorId,
        userAssembler::toUserSummaries,
        (post, authors) -> toPostResponse(post, authors.get(post.authorId()), requesterId));
  }

  public CursorPaginationResponse<GetPostsResponse.Post> toPostResponses(
      CursorPaginationResponse<PostDto> posts) {
    return toPostResponses(posts, null);
  }

  public List<GetPostsResponse.Post> toPostResponses(List<PostDto> posts, Long requesterId) {
    var authorIds = posts.stream().map(PostDto::authorId).distinct().toList();
    var authors = userAssembler.toUserSummaries(authorIds);

    return posts.stream()
        .map(post -> toPostResponse(post, authors.get(post.authorId()), requesterId))
        .toList();
  }

  public List<GetPostsResponse.Post> toPostResponses(List<PostDto> posts) {
    return toPostResponses(posts, null);
  }

  private PostSummary toPostSummary(PostDto post, UserSummary author, Long requesterId) {
    var project = post.projectHandle() == null ? null : postMapper.toProjectSummary(post);
    var isAuthor = requesterId != null && requesterId.equals(post.authorId());
    return postMapper.toPostSummary(post, author, project, isAuthor);
  }
}
