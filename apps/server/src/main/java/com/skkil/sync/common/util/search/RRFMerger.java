package com.skkil.sync.common.util.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RRFMerger {

  private RRFMerger() {}

  @SafeVarargs
  public static List<Long> merge(int k, List<Long>... lists) {
    Map<Long, Double> scores = new HashMap<>();

    for (List<Long> ranked : lists) {
      for (int i = 0; i < ranked.size(); i++) {
        int rank = i + 1;
        scores.merge(ranked.get(i), 1.0 / (k + rank), Double::sum);
      }
    }

    return scores.entrySet().stream()
        .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
        .map(Map.Entry::getKey)
        .toList();
  }
}
