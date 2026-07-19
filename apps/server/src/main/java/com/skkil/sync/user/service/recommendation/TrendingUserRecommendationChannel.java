package com.skkil.sync.user.service.recommendation;

import com.skkil.sync.user.model.UserRecommendationType;
import com.skkil.sync.user.repository.UserRecommendationQueryRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TrendingUserRecommendationChannel implements UserRecommendationChannel {

  private final UserRecommendationQueryRepository userRecommendationQueryRepository;

  public TrendingUserRecommendationChannel(
      UserRecommendationQueryRepository userRecommendationQueryRepository) {
    this.userRecommendationQueryRepository = userRecommendationQueryRepository;
  }

  @Override
  public UserRecommendationType getType() {
    return UserRecommendationType.TRENDING;
  }

  @Override
  public List<Long> getCandidates(Long requesterId, int limit) {
    return userRecommendationQueryRepository.findTrendingCandidateIds(requesterId, limit);
  }
}
