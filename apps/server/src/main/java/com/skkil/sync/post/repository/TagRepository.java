package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.Tag;
import com.skkil.sync.project.model.Project;
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
      WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))
        AND t.verified = TRUE
        AND t.project IS NULL
      LIMIT 10
      """)
  List<Tag> searchTags(String query);

  @Query(
      """
      SELECT t
      FROM Tag t
      WHERE t.project = :project AND t.verified = TRUE
      ORDER BY t.name ASC
      """)
  List<Tag> findByProject(Project project);

  @Query(
      """
      SELECT t
      FROM Tag t
      WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))
        AND t.verified = TRUE
        AND t.project = :project
      LIMIT 10
      """)
  List<Tag> searchTagsByProject(Project project, String query);

  @Query(
      """
      SELECT t
      FROM Tag t
      WHERE t.verified = FALSE
        AND t.project IS NULL
      ORDER BY t.createdAt ASC
      """)
  List<Tag> findUnverifiedTags();

  @Query(
      """
      SELECT t
      FROM Tag t
      WHERE t.verified = FALSE
        AND t.project = :project
      ORDER BY t.createdAt ASC
      """)
  List<Tag> findUnverifiedTagsByProject(Project project);

  @Query(
      """
      SELECT t
      FROM Tag t
      LEFT JOIN FETCH t.project
      WHERE t.id = :id
      """)
  Optional<Tag> findByIdWithProject(Long id);

  Optional<Tag> findByNameAndProjectIsNull(String name);

  Optional<Tag> findByNameAndProject(String name, Project project);

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
