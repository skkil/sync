package com.skkil.sync.bookmark.repository;

import com.skkil.sync.bookmark.model.PostBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {

  @Modifying
  @Query(
      value =
          """
          INSERT INTO post_bookmarks (user_id, post_id)
          VALUES (:userId, :postId)
          ON CONFLICT (user_id, post_id) DO NOTHING
          """,
      nativeQuery = true)
  void insertIfAbsent(Long userId, Long postId);

  void deleteByUser_IdAndPost_Id(Long userId, Long postId);
}
