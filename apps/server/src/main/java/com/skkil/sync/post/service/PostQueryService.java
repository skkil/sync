package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationProvider;
import com.skkil.sync.common.util.pagination.model.Cursor;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.mapper.PostAssembler;
import com.skkil.sync.post.model.PostScope;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.repository.pagination.CommentedPostCursorPaginationProvider;
import com.skkil.sync.post.repository.pagination.PostCursorPaginationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PostQueryService {

  private final PostQueryRepository postQueryRepository;
  private final PostContentMediaService contentMediaService;
  private final PostAssembler postAssembler;

  private final PaginationService paginationService;
  private final PostCursorPaginationProvider paginationProvider;
  private final CommentedPostCursorPaginationProvider commentedPostPaginationProvider;

  public PostQueryService(
      PostQueryRepository postQueryRepository,
      PostContentMediaService contentMediaService,
      PostAssembler postAssembler,
      PostCursorPaginationProvider paginationProvider,
      CommentedPostCursorPaginationProvider commentedPostPaginationProvider,
      PaginationService paginationService) {
    this.postQueryRepository = postQueryRepository;
    this.contentMediaService = contentMediaService;
    this.postAssembler = postAssembler;
    this.paginationProvider = paginationProvider;
    this.commentedPostPaginationProvider = commentedPostPaginationProvider;
    this.paginationService = paginationService;
  }

  @Transactional(readOnly = true)
  public GetPostsResponse getPosts(Long requesterId, CursorPaginationRequest pagination) {
    return getPostsResponse(requesterId, postQueryRepository.getPosts(requesterId), pagination);
  }

  @Transactional(readOnly = true)
  public GetPostResponse getPostBySlug(Long requesterId, String slug) {
    var post =
        postQueryRepository
            .getPostBySlug(requesterId, slug)
            .orElseThrow(() -> new PostNotFoundException(slug));

    var media = contentMediaService.getMediaFilesForPost(post.id());

    return postAssembler.toGetPostResponse(post, media, requesterId);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("#projectHandle == null or hasPermission(#projectHandle, 'PROJECT', 'READ')")
  public GetPostsResponse getDrafts(
      Long requesterId,
      PostType type,
      PostScope scope,
      String projectHandle,
      CursorPaginationRequest pagination) {
    return getPostsResponse(
        requesterId,
        postQueryRepository.getDraftsByAuthor(requesterId, type, scope, projectHandle),
        pagination);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#userId, 'PROFILE', 'READ')")
  public GetPostsResponse getUserPosts(
      Long requesterId, Long userId, PostType type, CursorPaginationRequest pagination) {
    return getPostsResponse(
        requesterId, postQueryRepository.getPostsByUser(requesterId, userId, type), pagination);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#tagId, 'TAG', 'READ')")
  public GetPostsResponse getPostsByTag(
      Long requesterId, Long tagId, PostType type, CursorPaginationRequest pagination) {
    return getPostsResponse(
        requesterId, postQueryRepository.getPostsByTag(requesterId, tagId, type), pagination);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#handle, 'PROJECT', 'READ')")
  public GetPostsResponse getPostsByProject(
      Long requesterId,
      String handle,
      PostType type,
      String authorHandle,
      CursorPaginationRequest pagination) {
    return getPostsResponse(
        requesterId,
        postQueryRepository.getPostsByProject(requesterId, handle, type, authorHandle),
        pagination);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#userId, 'PROFILE', 'READ')")
  public GetPostsResponse getCommentedPosts(
      Long requesterId, Long userId, String projectHandle, CursorPaginationRequest pagination) {
    return getPostsResponse(
        requesterId,
        postQueryRepository.getCommentedPosts(userId, projectHandle),
        commentedPostPaginationProvider,
        pagination);
  }

  private GetPostsResponse getPostsResponse(
      Long requesterId,
      CursorPaginationDataFetcher<PostDto> fetcher,
      CursorPaginationRequest pagination) {
    return getPostsResponse(requesterId, fetcher, paginationProvider, pagination);
  }

  private <C extends Cursor> GetPostsResponse getPostsResponse(
      Long requesterId,
      CursorPaginationDataFetcher<PostDto> fetcher,
      CursorPaginationProvider<PostDto, C> provider,
      CursorPaginationRequest pagination) {
    var page = paginationService.paginate(fetcher, provider, pagination);
    var posts = postAssembler.toPostResponses(page, requesterId);

    return new GetPostsResponse(posts);
  }
}
