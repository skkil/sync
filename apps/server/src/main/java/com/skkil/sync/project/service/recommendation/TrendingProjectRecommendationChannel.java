package com.skkil.sync.project.service.recommendation;

import com.skkil.sync.project.model.ProjectRecommendationType;
import com.skkil.sync.project.repository.ProjectRecommendationQueryRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TrendingProjectRecommendationChannel implements ProjectRecommendationChannel {

  private final ProjectRecommendationQueryRepository projectRecommendationQueryRepository;

  public TrendingProjectRecommendationChannel(
      ProjectRecommendationQueryRepository projectRecommendationQueryRepository) {
    this.projectRecommendationQueryRepository = projectRecommendationQueryRepository;
  }

  @Override
  public ProjectRecommendationType getType() {
    return ProjectRecommendationType.TRENDING;
  }

  @Override
  public List<Long> getCandidates(Long requesterId, int limit) {
    return projectRecommendationQueryRepository.findTrendingCandidateIds(requesterId, limit);
  }
}
