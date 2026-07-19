package com.skkil.sync.user.service.recommendation;

import com.skkil.sync.user.model.UserRecommendationType;
import com.skkil.sync.user.repository.UserRecommendationQueryRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProjectOverlapUserRecommendationChannel implements UserRecommendationChannel {

  private final UserRecommendationQueryRepository userRecommendationQueryRepository;

  public ProjectOverlapUserRecommendationChannel(
      UserRecommendationQueryRepository userRecommendationQueryRepository) {
    this.userRecommendationQueryRepository = userRecommendationQueryRepository;
  }

  @Override
  public UserRecommendationType getType() {
    return UserRecommendationType.PROJECT_OVERLAP;
  }

  @Override
  public List<Long> getCandidates(Long requesterId, int limit) {
    return userRecommendationQueryRepository.findProjectOverlapCandidateIds(requesterId, limit);
  }
}
