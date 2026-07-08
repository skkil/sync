package com.skkil.sync.project.repository;

import com.skkil.sync.project.model.ProjectFollowRelationship;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProjectFollowRelationshipRepository
    extends JpaRepository<ProjectFollowRelationship, Long> {

  @EntityGraph(attributePaths = {"project"})
  List<ProjectFollowRelationship> findByFollowerId(Long followerId);

  @Query(
      """
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM ProjectFollowRelationship r
            WHERE r.follower.id = :followerId AND r.project.id = :projectId
            """)
  public boolean existsByFollowerAndProject(Long followerId, Long projectId);

  @Modifying
  @Query(
      """
            DELETE FROM ProjectFollowRelationship r
            WHERE r.follower.id = :followerId AND r.project.id = :projectId
            """)
  public int deleteByFollowerAndProject(Long followerId, Long projectId);
}
