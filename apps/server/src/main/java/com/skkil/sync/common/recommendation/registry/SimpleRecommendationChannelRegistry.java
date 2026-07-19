package com.skkil.sync.common.recommendation.registry;

import com.skkil.sync.common.recommendation.channel.SimpleRecommendationChannel;
import com.skkil.sync.common.recommendation.merger.CandidateMerger;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class SimpleRecommendationChannelRegistry<TType extends Enum<TType>, TCandidate>
    extends RecommendationChannelRegistry<TType, SimpleRecommendationChannel<TType, TCandidate>> {

  private final CandidateMerger<TCandidate> merger;

  public SimpleRecommendationChannelRegistry(
      Class<TType> typeClass,
      TType defaultType,
      List<? extends SimpleRecommendationChannel<TType, TCandidate>> channels,
      CandidateMerger<TCandidate> merger) {
    super(typeClass, defaultType, channels);
    this.merger = merger;
  }

  public List<TCandidate> fetch(Long requester, int limit) {
    return fetch(requester, types, limit);
  }

  public List<TCandidate> fetch(Long requester, @Nullable TType type, int limit) {
    return resolve(type).getCandidates(requester, limit);
  }

  public List<TCandidate> fetch(Long requester, List<TType> selectedTypes, int limit) {
    List<TType> priority = selectedTypes.isEmpty() ? types : selectedTypes;

    // 우선순위가 높은 채널부터 순차적으로 조회하며, limit을 채우면 남은 채널은 조회하지 않는다.
    List<List<TCandidate>> signals = new ArrayList<>();
    List<TCandidate> merged = List.of();
    for (TType type : priority) {
      signals.add(resolve(type).getCandidates(requester, limit));
      merged = merger.merge(signals, limit);
      if (merged.size() >= limit) {
        return merged;
      }
    }

    return merged;
  }
}
