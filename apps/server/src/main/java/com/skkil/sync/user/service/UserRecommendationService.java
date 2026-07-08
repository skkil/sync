package com.skkil.sync.user.service;

import com.skkil.sync.user.dto.response.GetUserRecommendationsResponse;
import com.skkil.sync.user.mapper.UserAssembler;
import com.skkil.sync.user.mapper.UserRecommendationAssembler;
import com.skkil.sync.user.repository.UserRecommendationQueryRepository;
import java.util.LinkedHashSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRecommendationService {

  private static final int CANDIDATES_PER_SIGNAL = 35;

  // Explore 페이지의 캐러셀에 노출할 추천 사용자 수
  public static final int RECOMMENDATION_LIMIT = 15;

  private final UserRecommendationQueryRepository userRecommendationQueryRepository;
  private final UserAssembler userAssembler;
  private final UserRecommendationAssembler userRecommendationAssembler;

  public UserRecommendationService(
      UserRecommendationQueryRepository userRecommendationQueryRepository,
      UserAssembler userAssembler,
      UserRecommendationAssembler userRecommendationAssembler) {
    this.userRecommendationQueryRepository = userRecommendationQueryRepository;
    this.userAssembler = userAssembler;
    this.userRecommendationAssembler = userRecommendationAssembler;
  }

  @Transactional(readOnly = true)
  public GetUserRecommendationsResponse getRecommendations(Long userId) {
    var projectOverlapIds =
        userRecommendationQueryRepository.findProjectOverlapCandidateIds(
            userId, CANDIDATES_PER_SIGNAL);
    var trendingIds =
        userRecommendationQueryRepository.findTrendingCandidateIds(userId, CANDIDATES_PER_SIGNAL);

    var candidateIds = new LinkedHashSet<Long>();
    candidateIds.addAll(projectOverlapIds);
    candidateIds.addAll(trendingIds);

    if (candidateIds.size() < RECOMMENDATION_LIMIT) {
      candidateIds.addAll(
          userRecommendationQueryRepository.findRecentlyJoinedCandidateIds(
              userId, CANDIDATES_PER_SIGNAL));
    }

    var orderedIds = candidateIds.stream().limit(RECOMMENDATION_LIMIT).toList();

    var summaries = userAssembler.toUserSummaries(orderedIds);

    return userRecommendationAssembler.toGetUserRecommendationsResponse(orderedIds, summaries);
  }
}
