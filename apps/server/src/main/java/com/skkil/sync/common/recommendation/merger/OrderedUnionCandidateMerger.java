package com.skkil.sync.common.recommendation.merger;

import java.util.LinkedHashSet;
import java.util.List;

public class OrderedUnionCandidateMerger<TCandidate> implements CandidateMerger<TCandidate> {

  @Override
  public List<TCandidate> merge(List<List<TCandidate>> sources, int limit) {
    var merged = new LinkedHashSet<TCandidate>();
    for (var source : sources) {
      merged.addAll(source);
    }
    return merged.stream().limit(limit).toList();
  }
}
