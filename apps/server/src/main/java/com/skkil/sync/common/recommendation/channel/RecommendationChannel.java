package com.skkil.sync.common.recommendation.channel;

public interface RecommendationChannel<T extends Enum<T>> {

  T getType();
}
