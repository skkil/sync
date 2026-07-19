package com.skkil.sync.post.service.recommendation;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.post.dto.data.PostRecommendationCandidate;
import com.skkil.sync.post.dto.data.PostRecommendationCursor;
import com.skkil.sync.post.model.PostRecommendationType;
import com.skkil.sync.post.repository.PostRecommendationQueryRepository;
import com.skkil.sync.post.repository.pagination.PostRecommendationPaginationProvider;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

@Component
public class RecentPostRecommendationChannel implements PostRecommendationChannel {

  private final PostRecommendationQueryRepository postRecommendationQueryRepository;

  public RecentPostRecommendationChannel(
      PostRecommendationQueryRepository postRecommendationQueryRepository) {
    this.postRecommendationQueryRepository = postRecommendationQueryRepository;
  }

  @Override
  public PostRecommendationType getType() {
    return PostRecommendationType.RECENT;
  }

  @Override
  public CursorPaginationDataFetcher<PostRecommendationCandidate> getCandidateFetcher(
      Long requesterId) {
    return postRecommendationQueryRepository.getCandidates(DSL.noCondition());
  }

  @Override
  public KeysetCursorPaginationProvider<PostRecommendationCandidate, PostRecommendationCursor>
      getPaginationProvider() {
    return PostRecommendationPaginationProvider.CREATED_AT;
  }
}
