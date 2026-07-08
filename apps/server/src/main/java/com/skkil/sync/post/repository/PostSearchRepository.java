package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.PostEmbedding;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.SearchResults;
import org.springframework.data.domain.Vector;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface PostSearchRepository extends Repository<PostEmbedding, Long> {

  SearchResults<PostEmbedding> findByEmbeddingNear(Vector embedding, Limit limit);

  @Query(
      value =
          """
          SELECT pe.post_id FROM post_embeddings pe
          JOIN posts p ON p.id = pe.post_id
          LEFT JOIN projects pr ON pr.id = p.project_id
          WHERE (:projectHandle IS NULL OR pr.handle = :projectHandle)
          ORDER BY pe.embedding <=> :embedding
          LIMIT :n
          """,
      nativeQuery = true)
  List<Long> findTopNByEmbeddingNearAndProjectHandle(
      @Param("embedding") Vector embedding,
      @Param("projectHandle") @Nullable String projectHandle,
      int n);

  @Query(
      value =
          """
          SELECT r.id FROM posts r
          LEFT JOIN projects pr ON pr.id = r.project_id
          WHERE r.visibility = 'VISIBLE'
          AND r.status = 'PUBLISHED'
          AND ((:projectHandle IS NULL AND r.project_id IS NULL) OR pr.handle = :projectHandle)
          AND r.content ILIKE '%' || :query || '%'
          ORDER BY similarity(r.content, :query) DESC
          LIMIT :n
          """,
      nativeQuery = true)
  List<Long> findTopNByFullTextSearch(
      @Param("query") String query, @Param("projectHandle") @Nullable String projectHandle, int n);
}
