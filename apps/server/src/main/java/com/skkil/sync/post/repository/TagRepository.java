package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostVisibility;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.project.model.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {

  Page<Tag> findByProjectIsNullAndVerifiedTrueOrderByNameAsc(Pageable pageable);

  @Query(
      """
      SELECT t
      FROM Tag t
      WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))
        AND t.project IS NULL
        AND (
          t.verified = TRUE
          OR EXISTS (
            SELECT pt.id
            FROM PostTag pt
            WHERE pt.tag = t
              AND pt.post.project IS NULL
              AND pt.post.status = :status
              AND pt.post.visibility = :visibility
          )
        )
      ORDER BY t.postCount DESC, t.name ASC
      LIMIT 10
      """)
  List<Tag> searchTags(String query, PostStatus status, PostVisibility visibility);

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

  Optional<Tag> findByIdAndProjectIsNull(Long id);

  boolean existsByProjectIsNull();

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

  @Modifying
  @Query(
      """
      UPDATE Tag t
      SET t.followerCount = t.followerCount + 1
      WHERE t = :tag
      """)
  void incrementFollowerCount(Tag tag);

  @Modifying
  @Query(
      """
      UPDATE Tag t
      SET t.followerCount = t.followerCount - 1
      WHERE t = :tag AND t.followerCount > 0
      """)
  void decrementFollowerCount(Tag tag);

  @Modifying
  @Query(
      value =
          "DELETE FROM post_tags WHERE tag_id = :sourceTagId AND post_id IN "
              + "(SELECT post_id FROM post_tags WHERE tag_id = :targetTagId)",
      nativeQuery = true)
  void deleteDuplicatePostTags(Long sourceTagId, Long targetTagId);

  @Modifying
  @Query(
      value = "UPDATE post_tags SET tag_id = :targetTagId WHERE tag_id = :sourceTagId",
      nativeQuery = true)
  void reassignPostTags(Long sourceTagId, Long targetTagId);

  @Modifying
  @Query(
      value =
          "DELETE FROM tag_follow_relationships WHERE tag_id = :sourceTagId AND follower_id IN "
              + "(SELECT follower_id FROM tag_follow_relationships WHERE tag_id = :targetTagId)",
      nativeQuery = true)
  void deleteDuplicateTagFollows(Long sourceTagId, Long targetTagId);

  @Modifying
  @Query(
      value =
          "UPDATE tag_follow_relationships SET tag_id = :targetTagId WHERE tag_id = :sourceTagId",
      nativeQuery = true)
  void reassignTagFollows(Long sourceTagId, Long targetTagId);

  @Modifying
  @Query(
      value =
          "UPDATE tags SET "
              + "post_count = (SELECT COUNT(*) FROM post_tags WHERE tag_id = :tagId), "
              + "follower_count = (SELECT COUNT(*) FROM tag_follow_relationships WHERE tag_id = :tagId) "
              + "WHERE id = :tagId",
      nativeQuery = true)
  void recomputeCounts(Long tagId);
}
