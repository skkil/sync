package com.skkil.sync.common.recommendation.channel;

import java.util.List;

public interface SimpleRecommendationChannel<TType extends Enum<TType>, TCandidate>
    extends RecommendationChannel<TType> {

  List<TCandidate> getCandidates(Long requesterId, int limit);
}
