package com.skkil.sync.bookmark.service;

import com.skkil.sync.bookmark.dto.response.GetBookmarkedReflectionsResponse;
import com.skkil.sync.bookmark.mapper.ReflectionBookmarkMapper;
import com.skkil.sync.bookmark.repository.ReflectionBookmarkQueryRepository;
import com.skkil.sync.bookmark.repository.pagination.BookmarkedReflectionCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.media.service.domain.MediaDomainService;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReflectionBookmarkQueryService {

  private final ReflectionBookmarkQueryRepository reflectionBookmarkQueryRepository;
  private final ReflectionBookmarkMapper reflectionBookmarkMapper;
  private final BookmarkedReflectionCursorPaginationProvider paginationProvider;
  private final PaginationService paginationService;
  private final MediaDomainService mediaDomainService;

  public ReflectionBookmarkQueryService(
      ReflectionBookmarkQueryRepository reflectionBookmarkQueryRepository,
      ReflectionBookmarkMapper reflectionBookmarkMapper,
      BookmarkedReflectionCursorPaginationProvider paginationProvider,
      PaginationService paginationService,
      MediaDomainService mediaDomainService) {
    this.reflectionBookmarkQueryRepository = reflectionBookmarkQueryRepository;
    this.reflectionBookmarkMapper = reflectionBookmarkMapper;
    this.paginationProvider = paginationProvider;
    this.paginationService = paginationService;
    this.mediaDomainService = mediaDomainService;
  }

  @Transactional(readOnly = true)
  public GetBookmarkedReflectionsResponse getBookmarkedReflections(
      Long userId, CursorPaginationRequest pagination) {
    var bookmarkedReflections =
        paginationService.paginate(
            reflectionBookmarkQueryRepository.getBookmarkedReflections(userId),
            paginationProvider,
            pagination);

    List<Long> profileImageIds = new ArrayList<>();
    for (var bookmarkedReflection : bookmarkedReflections) {
      if (bookmarkedReflection.authorProfileImageId() != null) {
        profileImageIds.add(bookmarkedReflection.authorProfileImageId());
      }
    }

    Map<Long, URL> profileImageUrls = mediaDomainService.generatePublicGetUrls(profileImageIds);

    return new GetBookmarkedReflectionsResponse(
        bookmarkedReflections.map(
            bookmarkedReflection ->
                reflectionBookmarkMapper.toBookmarkedReflectionResponse(
                    bookmarkedReflection, profileImageUrls)));
  }
}
