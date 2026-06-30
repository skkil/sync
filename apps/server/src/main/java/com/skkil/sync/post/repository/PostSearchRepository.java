package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.PostEmbedding;
import java.util.List;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.SearchResults;
import org.springframework.data.domain.Vector;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface PostSearchRepository extends Repository<PostEmbedding, Long> {

  SearchResults<PostEmbedding> findByEmbeddingNear(Vector embedding, Limit limit);

  @Query(
      value =
          """
          SELECT r.id FROM posts r
          WHERE r.visibility = 'VISIBLE'
          AND r.content ILIKE '%' || :query || '%'
          ORDER BY similarity(r.content, :query) DESC
          LIMIT :n
          """,
      nativeQuery = true)
  List<Long> findTopNByFullTextSearch(String query, int n);
}
