package com.skkil.sync.bookmark.service;

import com.skkil.sync.bookmark.dto.data.BookmarkedPostDto;
import com.skkil.sync.bookmark.dto.response.GetBookmarkedPostsResponse;
import com.skkil.sync.bookmark.mapper.PostBookmarkMapper;
import com.skkil.sync.bookmark.repository.PostBookmarkQueryRepository;
import com.skkil.sync.bookmark.repository.pagination.BookmarkedPostCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.media.service.domain.MediaDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostBookmarkQueryService {

  private final PostBookmarkQueryRepository postBookmarkQueryRepository;
  private final PostBookmarkMapper postBookmarkMapper;
  private final BookmarkedPostCursorPaginationProvider paginationProvider;
  private final PaginationService paginationService;
  private final MediaDomainService mediaDomainService;

  public PostBookmarkQueryService(
      PostBookmarkQueryRepository postBookmarkQueryRepository,
      PostBookmarkMapper postBookmarkMapper,
      BookmarkedPostCursorPaginationProvider paginationProvider,
      PaginationService paginationService,
      MediaDomainService mediaDomainService) {
    this.postBookmarkQueryRepository = postBookmarkQueryRepository;
    this.postBookmarkMapper = postBookmarkMapper;
    this.paginationProvider = paginationProvider;
    this.paginationService = paginationService;
    this.mediaDomainService = mediaDomainService;
  }

  @Transactional(readOnly = true)
  public GetBookmarkedPostsResponse getBookmarkedPosts(
      Long userId, CursorPaginationRequest pagination) {
    var bookmarkedPosts =
        paginationService
            .paginate(
                postBookmarkQueryRepository.getBookmarkedPosts(userId),
                paginationProvider,
                pagination)
            .mapWithLookup(
                BookmarkedPostDto::authorProfileImageId,
                mediaDomainService::generatePublicGetUrls,
                postBookmarkMapper::toBookmarkedPostResponse);

    return new GetBookmarkedPostsResponse(bookmarkedPosts);
  }
}
