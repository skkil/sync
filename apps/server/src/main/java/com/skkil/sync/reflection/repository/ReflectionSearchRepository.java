package com.skkil.sync.reflection.repository;

import com.skkil.sync.reflection.model.ReflectionEmbedding;
import java.util.List;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.SearchResults;
import org.springframework.data.domain.Vector;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface ReflectionSearchRepository extends Repository<ReflectionEmbedding, Long> {

  SearchResults<ReflectionEmbedding> findByEmbeddingNear(Vector embedding, Limit limit);

  @Query(
      value =
          """
          SELECT r.id FROM reflections r
          WHERE to_tsvector('english', r.content) @@ plainto_tsquery('english', :query)
          ORDER BY ts_rank(to_tsvector('english', r.content), plainto_tsquery('english', :query)) DESC
          LIMIT :n
          """,
      nativeQuery = true)
  List<Long> findTopNByFullTextSearch(String query, int n);
}
