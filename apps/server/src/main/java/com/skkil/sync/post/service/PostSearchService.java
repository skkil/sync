package com.skkil.sync.post.service;

import com.skkil.sync.common.recommendation.merger.CandidateMerger;
import com.skkil.sync.common.recommendation.merger.RRFCandidateMerger;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.SearchPostsResponse;
import com.skkil.sync.post.mapper.PostAssembler;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.repository.PostSearchRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Vector;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostSearchService {

  private static final int K = 60;
  private static final CandidateMerger<Long> MERGER = new RRFCandidateMerger<>(K);

  private final PostSearchRepository postSearchRepository;
  private final PostQueryRepository postQueryRepository;
  private final PostEmbeddingService postEmbeddingService;
  private final PostAssembler postAssembler;

  public PostSearchService(
      PostSearchRepository postSearchRepository,
      PostQueryRepository postQueryRepository,
      PostEmbeddingService postEmbeddingService,
      PostAssembler postAssembler) {
    this.postSearchRepository = postSearchRepository;
    this.postQueryRepository = postQueryRepository;
    this.postEmbeddingService = postEmbeddingService;
    this.postAssembler = postAssembler;
  }

  public SearchPostsResponse searchPosts(
      Long requesterId, String query, @Nullable String projectHandle) {
    // Spring binds a query param passed as `?projectHandle=` to "", not null — normalize so the
    // repository's `:projectHandle IS NULL` scope check treats blank the same as omitted.
    String normalizedProjectHandle =
        (projectHandle == null || projectHandle.isBlank()) ? null : projectHandle;

    try {
      List<Long> topNByEmbeddingSimilarity =
          findTopNByEmbeddingSimilarity(query, normalizedProjectHandle);
      List<Long> topNByFullTextSearch =
          postSearchRepository.findTopNByFullTextSearch(query, normalizedProjectHandle, K);

      List<Long> ids =
          MERGER.merge(List.of(topNByEmbeddingSimilarity, topNByFullTextSearch), 2 * K);
      List<PostDto> posts =
          normalizedProjectHandle == null
              ? postQueryRepository.getPostsByIds(requesterId, ids)
              : postQueryRepository.getPostsByIdsInProject(
                  requesterId, ids, normalizedProjectHandle);

      return new SearchPostsResponse(postAssembler.toPostResponses(posts, requesterId));
    } catch (Exception e) {
      log.error("Post search failed for query '{}'", query, e);
      return new SearchPostsResponse(List.of());
    }
  }

  private List<Long> findTopNByEmbeddingSimilarity(String query, @Nullable String projectHandle) {
    try {
      float[] queryEmbedding = postEmbeddingService.computeEmbedding(query);
      return postSearchRepository.findTopNByEmbeddingNearAndProjectHandle(
          Vector.of(queryEmbedding), projectHandle, K);
    } catch (Exception e) {
      // Embedding search is a best-effort signal layered on top of full-text search — an
      // unreachable or misbehaving embedding model should degrade search, not fail it outright.
      log.warn("Embedding search unavailable, falling back to full-text only", e);
      return List.of();
    }
  }
}
