package com.skkil.sync.post.service.recommendation;

import com.skkil.sync.post.model.TagRecommendationType;
import com.skkil.sync.post.repository.TagRecommendationQueryRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TrendingTagRecommendationChannel implements TagRecommendationChannel {

  private final TagRecommendationQueryRepository tagRecommendationQueryRepository;

  public TrendingTagRecommendationChannel(
      TagRecommendationQueryRepository tagRecommendationQueryRepository) {
    this.tagRecommendationQueryRepository = tagRecommendationQueryRepository;
  }

  @Override
  public TagRecommendationType getType() {
    return TagRecommendationType.TRENDING;
  }

  @Override
  public List<Long> getCandidates(Long requesterId, int limit) {
    return tagRecommendationQueryRepository.findTrendingCandidateIds(requesterId, limit);
  }
}
