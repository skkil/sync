package com.skkil.sync.project.repository;

import com.skkil.sync.project.model.Project;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  @Modifying
  @Query(
      value = "UPDATE projects SET follower_count = follower_count + 1 WHERE id = :projectId",
      nativeQuery = true)
  void incrementFollowerCount(Long projectId);

  @Modifying
  @Query(
      value =
          "UPDATE projects SET follower_count = GREATEST(follower_count - 1, 0) WHERE id = :projectId",
      nativeQuery = true)
  void decrementFollowerCount(Long projectId);

  @Query(
      """
      SELECT p
      FROM Project p
      WHERE p.isPublic = true
      AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.handle) LIKE LOWER(CONCAT('%', :query, '%')))
      LIMIT 10
      """)
  List<Project> searchProjects(String query);

  @Query(
      """
      SELECT p
      FROM Project p
      WHERE
      (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.handle) LIKE LOWER(CONCAT('%', :query, '%')))
      AND EXISTS (
       SELECT t
       FROM Teammate t
       WHERE
       t.project = p AND t.user.id = :userId
      )
      LIMIT 10
      """)
  List<Project> searchMyProjects(Long userId, String query);

  @Query(
      """
      SELECT p
      FROM Project p
      WHERE EXISTS (
       SELECT t
       FROM Teammate t
       WHERE
       t.project = p AND t.user.id = :userId
      )
      """)
  List<Project> findMyProjects(Long userId);

  @Query(
      """
      SELECT p
      FROM Project p
      WHERE p.isPublic = true
      AND EXISTS (
       SELECT t
       FROM Teammate t
       WHERE
       t.project = p AND t.user.id = :userId
      )
      """)
  List<Project> findPublicProjectsByUserId(Long userId);

  Optional<Project> findByHandle(String handle);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Project p WHERE p.handle = :handle")
  Optional<Project> findByHandleForUpdate(String handle);

  boolean existsByHandle(String handle);
}
