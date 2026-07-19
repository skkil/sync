package com.skkil.sync.post.repository;

import com.skkil.sync.post.model.TagFollowRelationship;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagFollowRelationshipRepository
    extends JpaRepository<TagFollowRelationship, Long> {

  @EntityGraph(attributePaths = {"tag"})
  Page<TagFollowRelationship> findByFollowerId(Long followerId, Pageable pageable);

  @Query("SELECT r.tag.id FROM TagFollowRelationship r WHERE r.follower.id = :followerId")
  Set<Long> findTagIdsByFollowerId(Long followerId);

  @Query(
      """
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM TagFollowRelationship r
            WHERE r.follower.id = :followerId AND r.tag.id = :tagId
            """)
  boolean existsByFollowerAndTag(Long followerId, Long tagId);

  @Modifying
  @Query(
      """
            DELETE FROM TagFollowRelationship r
            WHERE r.follower.id = :followerId AND r.tag.id = :tagId
            """)
  int deleteByFollowerAndTag(Long followerId, Long tagId);
}
