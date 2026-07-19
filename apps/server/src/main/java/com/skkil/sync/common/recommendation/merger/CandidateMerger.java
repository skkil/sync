package com.skkil.sync.common.recommendation.merger;

import java.util.List;

public interface CandidateMerger<TCandidate> {

  List<TCandidate> merge(List<List<TCandidate>> sources, int limit);
}
