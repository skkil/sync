package com.skkil.sync.bookmark.repository;

import com.skkil.sync.bookmark.model.ReflectionBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReflectionBookmarkRepository extends JpaRepository<ReflectionBookmark, Long> {

  @Modifying
  @Query(
      value =
          """
          INSERT INTO reflection_bookmarks (user_id, reflection_id)
          VALUES (:userId, :reflectionId)
          ON CONFLICT (user_id, reflection_id) DO NOTHING
          """,
      nativeQuery = true)
  void insertIfAbsent(Long userId, Long reflectionId);

  void deleteByUser_IdAndReflection_Id(Long userId, Long reflectionId);
}
