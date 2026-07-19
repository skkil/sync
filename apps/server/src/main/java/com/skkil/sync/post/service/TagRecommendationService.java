package com.skkil.sync.post.service;

import com.skkil.sync.common.recommendation.merger.OrderedUnionCandidateMerger;
import com.skkil.sync.common.recommendation.registry.SimpleRecommendationChannelRegistry;
import com.skkil.sync.post.dto.response.GetTagRecommendationsResponse;
import com.skkil.sync.post.mapper.TagMapper;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.model.TagRecommendationType;
import com.skkil.sync.post.repository.TagFollowRelationshipRepository;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.post.service.recommendation.TagRecommendationChannel;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagRecommendationService {

  public static final int RECOMMENDATION_LIMIT = 15;

  private final SimpleRecommendationChannelRegistry<TagRecommendationType, Long> channelRegistry;
  private final TagRepository tagRepository;
  private final TagFollowRelationshipRepository tagFollowRelationshipRepository;
  private final TagMapper tagMapper;

  public TagRecommendationService(
      List<TagRecommendationChannel> channels,
      TagRepository tagRepository,
      TagFollowRelationshipRepository tagFollowRelationshipRepository,
      TagMapper tagMapper) {
    this.channelRegistry =
        new SimpleRecommendationChannelRegistry<>(
            TagRecommendationType.class,
            TagRecommendationType.TRENDING,
            channels,
            new OrderedUnionCandidateMerger<>());
    this.tagRepository = tagRepository;
    this.tagFollowRelationshipRepository = tagFollowRelationshipRepository;
    this.tagMapper = tagMapper;
  }

  @Transactional(readOnly = true)
  public GetTagRecommendationsResponse getRecommendations(
      Long userId, @Nullable TagRecommendationType type) {
    var orderedIds =
        (type != null)
            ? channelRegistry.fetch(userId, type, RECOMMENDATION_LIMIT)
            : channelRegistry.fetch(userId, RECOMMENDATION_LIMIT);

    Set<Long> followedTagIds = tagFollowRelationshipRepository.findTagIdsByFollowerId(userId);
    Map<Long, Tag> tagsById =
        tagRepository.findAllById(orderedIds).stream()
            .collect(Collectors.toMap(Tag::getId, Function.identity()));

    var tags =
        orderedIds.stream()
            .map(tagsById::get)
            .filter(Objects::nonNull)
            .map(tag -> tagMapper.toTagSummary(tag, followedTagIds))
            .toList();

    return new GetTagRecommendationsResponse(tags);
  }
}
