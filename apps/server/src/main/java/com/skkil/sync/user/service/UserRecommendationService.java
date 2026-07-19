package com.skkil.sync.user.service;

import com.skkil.sync.common.recommendation.merger.OrderedUnionCandidateMerger;
import com.skkil.sync.common.recommendation.registry.SimpleRecommendationChannelRegistry;
import com.skkil.sync.user.dto.response.GetUserRecommendationsResponse;
import com.skkil.sync.user.mapper.UserAssembler;
import com.skkil.sync.user.mapper.UserRecommendationAssembler;
import com.skkil.sync.user.model.UserRecommendationType;
import com.skkil.sync.user.service.recommendation.UserRecommendationChannel;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRecommendationService {

  public static final int RECOMMENDATION_LIMIT = 15;

  private final SimpleRecommendationChannelRegistry<UserRecommendationType, Long> channelRegistry;
  private final UserAssembler userAssembler;
  private final UserRecommendationAssembler userRecommendationAssembler;

  public UserRecommendationService(
      List<UserRecommendationChannel> channels,
      UserAssembler userAssembler,
      UserRecommendationAssembler userRecommendationAssembler) {
    this.channelRegistry =
        new SimpleRecommendationChannelRegistry<>(
            UserRecommendationType.class,
            UserRecommendationType.PROJECT_OVERLAP,
            channels,
            new OrderedUnionCandidateMerger<>());
    this.userAssembler = userAssembler;
    this.userRecommendationAssembler = userRecommendationAssembler;
  }

  @Transactional(readOnly = true)
  public GetUserRecommendationsResponse getRecommendations(
      Long userId, @Nullable UserRecommendationType type) {
    var orderedIds =
        (type != null)
            ? channelRegistry.fetch(userId, type, RECOMMENDATION_LIMIT)
            : channelRegistry.fetch(userId, RECOMMENDATION_LIMIT);

    var summaries = userAssembler.toUserSummaries(orderedIds);

    return userRecommendationAssembler.toGetUserRecommendationsResponse(orderedIds, summaries);
  }
}
