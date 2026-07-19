package com.skkil.sync.post.mapper;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.media.dto.MediaDto;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.summary.PostSummary;
import com.skkil.sync.post.service.PostContentMediaService;
import com.skkil.sync.post.service.TagService;
import com.skkil.sync.user.mapper.UserAssembler;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PostAssembler {

  private final PostMapper postMapper;

  private final UserAssembler userAssembler;

  private final TagService tagService;

  private final PostContentMediaService postContentMediaService;

  public PostAssembler(
      PostMapper postMapper,
      UserAssembler userAssembler,
      TagService tagService,
      PostContentMediaService postContentMediaService) {
    this.postMapper = postMapper;
    this.userAssembler = userAssembler;
    this.tagService = tagService;
    this.postContentMediaService = postContentMediaService;
  }

  public GetPostResponse toGetPostResponse(PostDto post, List<MediaDto> media, Long requesterId) {
    var summary = materialize(List.of(post), requesterId).get(post.id());

    return GetPostResponse.builder()
        .summary(summary)
        .content(postMapper.toContent(post, media))
        .build();
  }

  public CursorPaginationResponse<PostSummary> toPostResponses(
      CursorPaginationResponse<PostDto> posts, Long requesterId) {
    var summaries =
        materialize(posts.nodes().stream().map(node -> node.content()).toList(), requesterId);
    return posts.map(post -> summaries.get(post.id()));
  }

  public List<PostSummary> toPostResponses(List<PostDto> posts, Long requesterId) {
    var summaries = materialize(posts, requesterId);
    return posts.stream().map(post -> summaries.get(post.id())).toList();
  }

  private Map<Long, PostSummary> materialize(List<PostDto> posts, Long requesterId) {
    var authorIds = posts.stream().map(PostDto::authorId).distinct().toList();
    var authors = userAssembler.toUserSummaries(authorIds);
    var postIds = posts.stream().map(PostDto::id).toList();
    var tagsByPostId = tagService.getTagsForPosts(requesterId, postIds);
    var previewMediaByPostId = postContentMediaService.getPreviewMediaForPosts(postIds);

    return posts.stream()
        .collect(
            Collectors.toMap(
                PostDto::id,
                post -> {
                  var project =
                      post.projectHandle() == null ? null : postMapper.toProjectSummary(post);
                  var isAuthor = requesterId != null && requesterId.equals(post.authorId());
                  var tags = tagsByPostId.getOrDefault(post.id(), List.of());
                  var previewMedia =
                      postMapper.toPreviewMedia(
                          previewMediaByPostId.getOrDefault(post.id(), List.of()));
                  return postMapper.toPostSummary(
                      post, authors.get(post.authorId()), project, isAuthor, tags, previewMedia);
                }));
  }
}
