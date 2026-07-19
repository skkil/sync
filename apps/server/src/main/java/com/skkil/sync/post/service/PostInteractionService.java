package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.mapper.PostAssembler;
import com.skkil.sync.post.repository.PostLikeRepository;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.repository.pagination.LikedPostCursorPaginationProvider;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostInteractionService {

  private final PostLikeRepository postLikeRepository;
  private final PostQueryRepository postQueryRepository;
  private final PostAssembler postAssembler;
  private final LikedPostCursorPaginationProvider paginationProvider;
  private final PaginationService paginationService;

  public PostInteractionService(
      PostLikeRepository postLikeRepository,
      PostQueryRepository postQueryRepository,
      PostAssembler postAssembler,
      LikedPostCursorPaginationProvider paginationProvider,
      PaginationService paginationService) {
    this.postLikeRepository = postLikeRepository;
    this.postQueryRepository = postQueryRepository;
    this.postAssembler = postAssembler;
    this.paginationProvider = paginationProvider;
    this.paginationService = paginationService;
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'READ')")
  public void likePost(Long userId, Long postId) {
    postLikeRepository.insertAndIncrementIfAbsent(userId, postId);
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'READ')")
  public void unlikePost(Long userId, Long postId) {
    postLikeRepository.deleteAndDecrementIfPresent(userId, postId);
  }

  @Transactional(readOnly = true)
  public GetPostsResponse getLikedPosts(
      Long userId, @Nullable String projectHandle, CursorPaginationRequest pagination) {
    var page =
        paginationService.paginate(
            postQueryRepository.getLikedPosts(userId, projectHandle),
            paginationProvider,
            pagination);
    var likedPosts = postAssembler.toPostResponses(page, userId);

    return new GetPostsResponse(likedPosts);
  }
}
