package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.mapper.PostMapper;
import com.skkil.sync.post.repository.PostQueryRepository;
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
  private final PostMapper postMapper;

  private final PaginationService paginationService;
  private final PostCursorPaginationProvider paginationProvider;

  public PostQueryService(
      PostQueryRepository postQueryRepository,
      PostContentMediaService contentMediaService,
      PostMapper postMapper,
      PostCursorPaginationProvider paginationProvider,
      PaginationService paginationService) {
    this.postQueryRepository = postQueryRepository;
    this.contentMediaService = contentMediaService;
    this.postMapper = postMapper;
    this.paginationProvider = paginationProvider;
    this.paginationService = paginationService;
  }

  @Transactional(readOnly = true)
  public GetPostsResponse getPosts(CursorPaginationRequest pagination) {
    var posts =
        paginationService
            .paginate(postQueryRepository.getPosts(), paginationProvider, pagination)
            .map(postMapper::toPostResponse);

    return new GetPostsResponse(posts);
  }

  @Transactional(readOnly = true)
  public GetPostResponse getPostBySlug(Long requesterId, String slug) {
    var post =
        postQueryRepository
            .getPostBySlug(requesterId, slug)
            .orElseThrow(() -> new PostNotFoundException(slug));

    var media = contentMediaService.getMediaFilesForPost(post.id());

    return postMapper.toGetPostResponse(post, media);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#userId, 'PROFILE', 'READ')")
  public GetPostsResponse getUserPosts(Long userId, CursorPaginationRequest pagination) {
    var posts =
        paginationService
            .paginate(postQueryRepository.getPostsByUser(userId), paginationProvider, pagination)
            .map(postMapper::toPostResponse);

    return new GetPostsResponse(posts);
  }

  @Transactional(readOnly = true)
  public GetPostsResponse getPostsByProject(String handle, CursorPaginationRequest pagination) {
    var posts =
        paginationService
            .paginate(postQueryRepository.getPostsByProject(handle), paginationProvider, pagination)
            .map(postMapper::toPostResponse);

    return new GetPostsResponse(posts);
  }
}
