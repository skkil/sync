package com.skkil.sync.reflection.service;

import com.skkil.sync.common.util.search.RRFMerger;
import com.skkil.sync.reflection.dto.data.ReflectionDto;
import com.skkil.sync.reflection.dto.response.SearchReflectionsResponse;
import com.skkil.sync.reflection.repository.ReflectionQueryRepository;
import com.skkil.sync.reflection.repository.ReflectionSearchRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Vector;
import org.springframework.stereotype.Service;

@Service
public class ReflectionSearchService {

  private final ReflectionSearchRepository reflectionSearchRepository;
  private final ReflectionQueryRepository reflectionQueryRepository;
  private final ReflectionEmbeddingService reflectionEmbeddingService;

  public ReflectionSearchService(
      ReflectionSearchRepository reflectionSearchRepository,
      ReflectionQueryRepository reflectionQueryRepository,
      ReflectionEmbeddingService reflectionEmbeddingService) {
    this.reflectionSearchRepository = reflectionSearchRepository;
    this.reflectionQueryRepository = reflectionQueryRepository;
    this.reflectionEmbeddingService = reflectionEmbeddingService;
  }

  public SearchReflectionsResponse searchReflections(String query) {
    float[] queryEmbedding = reflectionEmbeddingService.computeEmbedding(query);

    CompletableFuture<List<Long>>
        topNByEmbeddingSimilarity =
            CompletableFuture.supplyAsync(
                () ->
                    reflectionSearchRepository
                        .findByEmbeddingNear(Vector.of(queryEmbedding), Limit.of(60))
                        .stream()
                        .map(r -> r.getContent().getReflection().getId())
                        .toList()),
        topNByFullTextSearch =
            CompletableFuture.supplyAsync(
                () -> reflectionSearchRepository.findTopNByFullTextSearch(query, 60));

    CompletableFuture.allOf(topNByEmbeddingSimilarity, topNByFullTextSearch).join();

    List<Long> ids = RRFMerger.merge(topNByEmbeddingSimilarity.join(), topNByFullTextSearch.join());
    List<ReflectionDto> reflections = reflectionQueryRepository.getReflectionsByIds(ids);

    List<SearchReflectionsResponse.Reflection> results =
        reflections.stream()
            .map(
                dto ->
                    SearchReflectionsResponse.Reflection.builder()
                        .id(dto.id())
                        .content(dto.content())
                        .build())
            .toList();

    return new SearchReflectionsResponse(results);
  }
}
