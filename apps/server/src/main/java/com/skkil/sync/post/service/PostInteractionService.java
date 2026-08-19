package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.response.PaginatedGetPostsResponse;
import com.skkil.sync.post.event.PostLikedEvent;
import com.skkil.sync.post.mapper.PostAssembler;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.repository.PostLikeRepository;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.repository.pagination.LikedPostCursorPaginationProvider;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
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
  private final PostDomainService postDomainService;
  private final ApplicationEventPublisher eventPublisher;

  public PostInteractionService(
      PostLikeRepository postLikeRepository,
      PostQueryRepository postQueryRepository,
      PostAssembler postAssembler,
      LikedPostCursorPaginationProvider paginationProvider,
      PaginationService paginationService,
      PostDomainService postDomainService,
      ApplicationEventPublisher eventPublisher) {
    this.postLikeRepository = postLikeRepository;
    this.postQueryRepository = postQueryRepository;
    this.postAssembler = postAssembler;
    this.paginationProvider = paginationProvider;
    this.paginationService = paginationService;
    this.postDomainService = postDomainService;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'READ')")
  public void likePost(Long userId, Long postId) {
    int inserted = postLikeRepository.insertAndIncrementIfAbsent(userId, postId);
    if (inserted == 0) {
      // 이미 누른 좋아요 — PUT이 멱등이므로 알림도 멱등이어야 한다.
      return;
    }

    // 새 좋아요일 때만 작성자를 조회한다. 알림 리스너는 커밋 후에 발화한다.
    Post post = postDomainService.getPost(postId);
    eventPublisher.publishEvent(new PostLikedEvent(postId, post.getAuthor().getId(), userId));
  }

  @Transactional
  @PreAuthorize("hasPermission(#postId, 'POST', 'READ')")
  public void unlikePost(Long userId, Long postId) {
    postLikeRepository.deleteAndDecrementIfPresent(userId, postId);
  }

  @Transactional(readOnly = true)
  public PaginatedGetPostsResponse getLikedPosts(
      Long userId, @Nullable String projectHandle, CursorPaginationRequest pagination) {
    var page =
        paginationService.paginate(
            postQueryRepository.getLikedPosts(userId, projectHandle),
            paginationProvider,
            pagination);
    var likedPosts = postAssembler.toPostResponses(page, userId);

    return new PaginatedGetPostsResponse(likedPosts);
  }
}
