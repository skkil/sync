package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.GetPostRecommendationsResponse;
import com.skkil.sync.post.mapper.PostAssembler;
import com.skkil.sync.post.model.PostRecommendationType;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.service.recommendation.PostRecommendationChannel;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostRecommendationService {

  private final PostRecommendationChannel defaultChannel;
  private final Map<PostRecommendationType, PostRecommendationChannel> channelsByType;
  private final PostQueryRepository postQueryRepository;
  private final PostAssembler postAssembler;
  private final PaginationService paginationService;

  public PostRecommendationService(
      @Qualifier("recentPostRecommendationChannel") PostRecommendationChannel defaultChannel,
      @Qualifier("followingPostRecommendationChannel") PostRecommendationChannel followingChannel,
      @Qualifier("trendingPostRecommendationChannel") PostRecommendationChannel trendingChannel,
      PostQueryRepository postQueryRepository,
      PostAssembler postAssembler,
      PaginationService paginationService) {
    this.defaultChannel = defaultChannel;
    this.channelsByType = new EnumMap<>(PostRecommendationType.class);
    this.channelsByType.put(PostRecommendationType.FOLLOWING, followingChannel);
    this.channelsByType.put(PostRecommendationType.TRENDING, trendingChannel);
    this.postQueryRepository = postQueryRepository;
    this.postAssembler = postAssembler;
    this.paginationService = paginationService;
  }

  @Transactional(readOnly = true)
  public GetPostRecommendationsResponse getRecommendations(
      Long requesterId, PostRecommendationType type, CursorPaginationRequest pagination) {
    var recommendationChannel =
        type == null ? defaultChannel : channelsByType.getOrDefault(type, defaultChannel);
    var candidates =
        paginationService.paginate(
            recommendationChannel.getCandidateFetcher(requesterId),
            recommendationChannel.getPaginationProvider(),
            pagination);

    var ids = candidates.nodes().stream().map(node -> node.content().id()).toList();

    Map<Long, PostDto> postsById =
        postQueryRepository.getPostsByIds(requesterId, ids).stream()
            .collect(Collectors.toMap(PostDto::id, Function.identity()));

    var posts =
        postAssembler.toPostResponses(
            candidates.map(candidate -> postsById.get(candidate.id())), requesterId);

    return new GetPostRecommendationsResponse(posts);
  }
}
