package com.skkil.sync.post.service.recommendation;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.post.dto.data.PostRecommendationCandidate;
import com.skkil.sync.post.dto.data.PostRecommendationCursor;
import com.skkil.sync.post.repository.PostRecommendationQueryRepository;
import com.skkil.sync.post.repository.pagination.PostRecommendationPaginationProvider;
import org.springframework.stereotype.Component;

@Component("trendingPostRecommendationChannel")
public class TrendingPostRecommendationChannel implements PostRecommendationChannel {

  private final PostRecommendationQueryRepository postRecommendationQueryRepository;

  public TrendingPostRecommendationChannel(
      PostRecommendationQueryRepository postRecommendationQueryRepository) {
    this.postRecommendationQueryRepository = postRecommendationQueryRepository;
  }

  @Override
  public CursorPaginationDataFetcher<PostRecommendationCandidate> getCandidateFetcher(
      Long requesterId) {
    return postRecommendationQueryRepository.getCandidates(
        postRecommendationQueryRepository.trendingCondition());
  }

  @Override
  public KeysetCursorPaginationProvider<PostRecommendationCandidate, PostRecommendationCursor>
      getPaginationProvider() {
    return PostRecommendationPaginationProvider.LIKE_COUNT;
  }
}
