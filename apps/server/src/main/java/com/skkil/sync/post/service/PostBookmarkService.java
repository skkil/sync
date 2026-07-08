package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.mapper.PostAssembler;
import com.skkil.sync.post.repository.PostBookmarkRepository;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.repository.pagination.BookmarkedPostCursorPaginationProvider;
import com.skkil.sync.user.mapper.UserAssembler;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostBookmarkService {

  private final PostBookmarkRepository postBookmarkRepository;
  private final PostDomainService postDomainService;
  private final PostQueryRepository postQueryRepository;
  private final PostAssembler postAssembler;
  private final UserAssembler userAssembler;
  private final BookmarkedPostCursorPaginationProvider paginationProvider;
  private final PaginationService paginationService;

  public PostBookmarkService(
      PostBookmarkRepository postBookmarkRepository,
      PostDomainService postDomainService,
      PostQueryRepository postQueryRepository,
      PostAssembler postAssembler,
      UserAssembler userAssembler,
      BookmarkedPostCursorPaginationProvider paginationProvider,
      PaginationService paginationService) {
    this.postBookmarkRepository = postBookmarkRepository;
    this.postDomainService = postDomainService;
    this.postQueryRepository = postQueryRepository;
    this.postAssembler = postAssembler;
    this.userAssembler = userAssembler;
    this.paginationProvider = paginationProvider;
    this.paginationService = paginationService;
  }

  @Transactional
  public void bookmarkPost(Long userId, Long postId) {
    postDomainService.getPublicPublishedPost(postId);
    postBookmarkRepository.insertIfAbsent(userId, postId);
  }

  @Transactional
  public void unbookmarkPost(Long userId, Long postId) {
    postBookmarkRepository.deleteByUser_IdAndPost_Id(userId, postId);
  }

  @Transactional(readOnly = true)
  public GetPostsResponse getBookmarkedPosts(
      Long userId, @Nullable String projectHandle, CursorPaginationRequest pagination) {
    var bookmarkedPosts =
        paginationService
            .paginate(
                postQueryRepository.getBookmarkedPosts(userId, projectHandle),
                paginationProvider,
                pagination)
            .mapWithLookup(
                PostDto::authorId,
                userAssembler::toUserSummaries,
                (post, authors) ->
                    postAssembler.toBookmarkedPostResponse(
                        post, authors.get(post.authorId()), userId));

    return new GetPostsResponse(bookmarkedPosts);
  }
}
