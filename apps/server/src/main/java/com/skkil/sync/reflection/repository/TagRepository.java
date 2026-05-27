package com.skkil.sync.reflection.repository;

import com.skkil.sync.reflection.model.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {

  @Query(
      """
      SELECT t
      FROM Tag t
      WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) AND t.verified = TRUE
      LIMIT 10
      """)
  List<Tag> searchTags(String query);

  Optional<Tag> findByName(String name);

  @Modifying
  @Query(
      """
      UPDATE Tag t
      SET t.postCount = t.postCount + 1
      WHERE t = :tag
      """)
  void incrementPostCount(Tag tag);

  @Modifying
  @Query(
      """
      UPDATE Tag t
      SET t.postCount = t.postCount - 1
      WHERE t = :tag AND t.postCount > 0
      """)
  void decrementPostCount(Tag tag);
}
