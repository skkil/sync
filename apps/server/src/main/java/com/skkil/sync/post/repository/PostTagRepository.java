package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.PostTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

  @Query(
      """
      SELECT pt
      FROM PostTag pt
      JOIN FETCH pt.tag t
      LEFT JOIN FETCH t.project
      WHERE pt.post.id IN :postIds
      """)
  List<PostTag> findByPostIdIn(List<Long> postIds);
}
