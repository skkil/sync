package com.skkil.sync.post.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.GetPostRecommendationsResponse;
import com.skkil.sync.post.mapper.PostAssembler;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.service.recommendation.PostRecommendationChannel;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostRecommendationService {

  private final PostRecommendationChannel recommendationChannel;
  private final PostQueryRepository postQueryRepository;
  private final PostAssembler postAssembler;
  private final PaginationService paginationService;

  public PostRecommendationService(
      @Qualifier("recentPostRecommendationChannel") PostRecommendationChannel recommendationChannel,
      PostQueryRepository postQueryRepository,
      PostAssembler postAssembler,
      PaginationService paginationService) {
    this.recommendationChannel = recommendationChannel;
    this.postQueryRepository = postQueryRepository;
    this.postAssembler = postAssembler;
    this.paginationService = paginationService;
  }

  @Transactional(readOnly = true)
  public GetPostRecommendationsResponse getRecommendations(
      Long requesterId, CursorPaginationRequest pagination) {
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
