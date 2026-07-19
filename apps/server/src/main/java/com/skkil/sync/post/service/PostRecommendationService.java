package com.skkil.sync.post.service;

import com.skkil.sync.common.recommendation.registry.InfiniteRecommendationChannelRegistry;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.data.PostRecommendationCandidate;
import com.skkil.sync.post.dto.response.GetPostRecommendationsResponse;
import com.skkil.sync.post.mapper.PostAssembler;
import com.skkil.sync.post.model.PostRecommendationType;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.service.recommendation.PostRecommendationChannel;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostRecommendationService {

  private final PostQueryRepository postQueryRepository;
  private final PostAssembler postAssembler;
  private final InfiniteRecommendationChannelRegistry<
          PostRecommendationType, PostRecommendationCandidate>
      channelRegistry;

  public PostRecommendationService(
      PostQueryRepository postQueryRepository,
      PostAssembler postAssembler,
      PaginationService paginationService,
      List<PostRecommendationChannel> channels) {
    this.postQueryRepository = postQueryRepository;
    this.postAssembler = postAssembler;
    this.channelRegistry =
        new InfiniteRecommendationChannelRegistry<>(
            PostRecommendationType.class,
            PostRecommendationType.RECENT,
            channels,
            paginationService);
  }

  @Transactional(readOnly = true)
  public GetPostRecommendationsResponse getRecommendations(
      Long requesterId, @Nullable PostRecommendationType type, CursorPaginationRequest pagination) {
    var candidates =
        (type != null)
            ? channelRegistry.fetch(requesterId, type, pagination)
            : channelRegistry.fetch(requesterId, pagination);

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
