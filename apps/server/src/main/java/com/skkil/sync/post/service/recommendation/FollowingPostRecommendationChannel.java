package com.skkil.sync.post.service.recommendation;

import com.skkil.sync.common.util.pagination.interfaces.CursorPaginationDataFetcher;
import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.post.dto.data.PostRecommendationCandidate;
import com.skkil.sync.post.dto.data.PostRecommendationCursor;
import com.skkil.sync.post.model.PostRecommendationType;
import com.skkil.sync.post.repository.PostRecommendationQueryRepository;
import com.skkil.sync.post.repository.pagination.PostRecommendationPaginationProvider;
import org.springframework.stereotype.Component;

@Component
public class FollowingPostRecommendationChannel implements PostRecommendationChannel {

  private final PostRecommendationQueryRepository postRecommendationQueryRepository;

  public FollowingPostRecommendationChannel(
      PostRecommendationQueryRepository postRecommendationQueryRepository) {
    this.postRecommendationQueryRepository = postRecommendationQueryRepository;
  }

  @Override
  public PostRecommendationType getType() {
    return PostRecommendationType.FOLLOWING;
  }

  @Override
  public CursorPaginationDataFetcher<PostRecommendationCandidate> getCandidateFetcher(
      Long requesterId) {
    return postRecommendationQueryRepository.getCandidates(
        postRecommendationQueryRepository.followingCondition(requesterId));
  }

  @Override
  public KeysetCursorPaginationProvider<PostRecommendationCandidate, PostRecommendationCursor>
      getPaginationProvider() {
    return PostRecommendationPaginationProvider.CREATED_AT;
  }
}
