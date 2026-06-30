package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  @Modifying
  @Query(
      value =
          """
          WITH ins AS (
            INSERT INTO post_likes (user_id, post_id)
            VALUES (:userId, :postId)
            ON CONFLICT (user_id, post_id) DO NOTHING
            RETURNING id
          )
          UPDATE posts SET like_count = like_count + 1
          WHERE id = :postId AND EXISTS (SELECT 1 FROM ins)
          """,
      nativeQuery = true)
  void insertAndIncrementIfAbsent(Long userId, Long postId);

  @Modifying
  @Query(
      value =
          """
          WITH del AS (
            DELETE FROM post_likes
            WHERE user_id = :userId AND post_id = :postId
            RETURNING id
          )
          UPDATE posts SET like_count = GREATEST(like_count - 1, 0)
          WHERE id = :postId AND EXISTS (SELECT 1 FROM del)
          """,
      nativeQuery = true)
  void deleteAndDecrementIfPresent(Long userId, Long postId);
}
