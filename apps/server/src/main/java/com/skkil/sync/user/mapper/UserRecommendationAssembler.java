package com.skkil.sync.user.mapper;

import com.skkil.sync.user.dto.response.GetUserRecommendationsResponse;
import com.skkil.sync.user.dto.summary.UserSummary;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UserRecommendationAssembler {

  public GetUserRecommendationsResponse toGetUserRecommendationsResponse(
      List<Long> orderedIds, Map<Long, UserSummary> summaries) {
    var users =
        orderedIds.stream()
            .filter(summaries::containsKey)
            .map(id -> new GetUserRecommendationsResponse.User(id.toString(), summaries.get(id)))
            .toList();

    return new GetUserRecommendationsResponse(users);
  }
}
