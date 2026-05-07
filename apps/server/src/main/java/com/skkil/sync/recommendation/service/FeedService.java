package com.skkil.sync.recommendation.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.recommendation.dto.response.GetFeedResponse;
import com.skkil.sync.recommendation.mapper.FeedMapper;
import com.skkil.sync.recommendation.repository.FeedQueryRepository;
import com.skkil.sync.recommendation.repository.pagination.FeedCursorPaginationProvider;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class FeedService {

  private final FeedQueryRepository feedQueryRepository;
  private final FeedMapper feedMapper;
  private final PaginationService paginationService;
  private final FeedCursorPaginationProvider paginationProvider;
  private final MediaDomainService mediaDomainService;

  public FeedService(
      FeedQueryRepository feedQueryRepository,
      FeedMapper feedMapper,
      PaginationService paginationService,
      FeedCursorPaginationProvider paginationProvider,
      MediaDomainService mediaDomainService) {
    this.feedQueryRepository = feedQueryRepository;
    this.feedMapper = feedMapper;
    this.paginationService = paginationService;
    this.paginationProvider = paginationProvider;
    this.mediaDomainService = mediaDomainService;
  }

  @Transactional(readOnly = true)
  public GetFeedResponse getRecentFeed(Long requesterId, CursorPaginationRequest pagination) {
    var feedItems =
        paginationService.paginate(
            feedQueryRepository.getRecentReflections(requesterId), paginationProvider, pagination);

    List<Long> profileImageIds = new ArrayList<>();

    for (var feedItem : feedItems) {
      profileImageIds.add(feedItem.authorProfileImageId());
    }

    Map<Long, URL> profileImageUrls = mediaDomainService.generatePublicGetUrls(profileImageIds);

    return new GetFeedResponse(
        feedItems.map(feedItem -> feedMapper.toFeedItemResponse(feedItem, profileImageUrls)));
  }
}
