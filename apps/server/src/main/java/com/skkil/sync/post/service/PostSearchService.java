package com.skkil.sync.post.service;

import com.skkil.sync.common.util.search.RRFMerger;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.SearchPostsResponse;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.repository.PostSearchRepository;
import java.util.List;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Vector;
import org.springframework.stereotype.Service;

@Service
public class PostSearchService {

  private final PostSearchRepository postSearchRepository;
  private final PostQueryRepository postQueryRepository;
  private final PostEmbeddingService postEmbeddingService;

  public PostSearchService(
      PostSearchRepository postSearchRepository,
      PostQueryRepository postQueryRepository,
      PostEmbeddingService postEmbeddingService) {
    this.postSearchRepository = postSearchRepository;
    this.postQueryRepository = postQueryRepository;
    this.postEmbeddingService = postEmbeddingService;
  }

  public SearchPostsResponse searchPosts(String query) {
    int k = 60;

    float[] queryEmbedding = postEmbeddingService.computeEmbedding(query);

    List<Long>
        topNByEmbeddingSimilarity =
            postSearchRepository
                .findByEmbeddingNear(Vector.of(queryEmbedding), Limit.of(k))
                .stream()
                .map(r -> r.getContent().getPost().getId())
                .toList(),
        topNByFullTextSearch = postSearchRepository.findTopNByFullTextSearch(query, k);

    List<Long> ids = RRFMerger.merge(k, topNByEmbeddingSimilarity, topNByFullTextSearch);
    List<PostDto> posts = postQueryRepository.getPostsByIds(ids);

    List<SearchPostsResponse.Post> results =
        posts.stream()
            .map(
                dto ->
                    SearchPostsResponse.Post.builder().id(dto.id()).content(dto.content()).build())
            .toList();

    return new SearchPostsResponse(results);
  }
}
