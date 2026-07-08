package com.skkil.sync.post.service;

import com.skkil.sync.common.util.search.RRFMerger;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.SearchPostsResponse;
import com.skkil.sync.post.mapper.PostAssembler;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.repository.PostSearchRepository;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Vector;
import org.springframework.stereotype.Service;

@Service
public class PostSearchService {

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
    int k = 60;

    float[] queryEmbedding = postEmbeddingService.computeEmbedding(query);

    List<Long>
        topNByEmbeddingSimilarity =
            postSearchRepository.findTopNByEmbeddingNearAndProjectHandle(
                Vector.of(queryEmbedding), projectHandle, k),
        topNByFullTextSearch =
            postSearchRepository.findTopNByFullTextSearch(query, projectHandle, k);

    List<Long> ids = RRFMerger.merge(k, topNByEmbeddingSimilarity, topNByFullTextSearch);
    List<PostDto> posts =
        projectHandle == null
            ? postQueryRepository.getPostsByIds(requesterId, ids)
            : postQueryRepository.getPostsByIdsInProject(requesterId, ids, projectHandle);

    return new SearchPostsResponse(postAssembler.toPostResponses(posts, requesterId));
  }
}
