package com.skkil.sync.common.recommendation.merger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RRFCandidateMerger<TCandidate> implements CandidateMerger<TCandidate> {

  private final int k;

  public RRFCandidateMerger(int k) {
    this.k = k;
  }

  @Override
  public List<TCandidate> merge(List<List<TCandidate>> sources, int limit) {
    Map<TCandidate, Double> scores = new HashMap<>();

    for (List<TCandidate> ranked : sources) {
      for (int i = 0; i < ranked.size(); i++) {
        scores.merge(ranked.get(i), 1.0 / (k + i + 1), Double::sum);
      }
    }

    return scores.entrySet().stream()
        .sorted(Map.Entry.<TCandidate, Double>comparingByValue().reversed())
        .map(Map.Entry::getKey)
        .limit(limit)
        .toList();
  }
}
