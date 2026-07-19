package com.skkil.sync.common.recommendation.registry;

import com.skkil.sync.common.recommendation.channel.RecommendationChannel;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * 추천 채널을 타입별로 등록/조회하는 공통 기반 클래스. 채널 조회(fetch)의 요청/응답 타입은 채널 종류마다 다르므로, 여기서는 채널 등록과 {@link #resolve}
 * 만 공유하고 실제 fetch API 는 각 하위 레지스트리가 정의한다.
 */
public abstract class RecommendationChannelRegistry<
    TType extends Enum<TType>, TChannel extends RecommendationChannel<TType>> {

  protected final TType defaultType;

  protected final List<TType> types;

  private final Map<TType, TChannel> channelsMap;

  protected RecommendationChannelRegistry(
      Class<TType> typeClass, TType defaultType, List<? extends TChannel> channels) {
    this.defaultType = defaultType;
    this.types = List.of(typeClass.getEnumConstants());

    this.channelsMap = new EnumMap<>(typeClass);
    for (var channel : channels) {
      var previous = channelsMap.put(channel.getType(), channel);
      if (previous != null) {
        throw new IllegalStateException(
            "Duplicate channel registered for type " + channel.getType());
      }
    }

    for (TType type : types) {
      if (!channelsMap.containsKey(type)) {
        throw new IllegalStateException("No channel registered for type " + type);
      }
    }
  }

  protected TChannel resolve(@Nullable TType type) {
    TType resolvedType = type == null ? defaultType : type;

    var channel = channelsMap.get(resolvedType);
    if (channel == null) {
      throw new IllegalStateException("No channel registered for type " + resolvedType);
    }

    return channel;
  }
}
