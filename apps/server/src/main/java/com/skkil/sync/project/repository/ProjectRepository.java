package com.skkil.sync.project.repository;

import com.skkil.sync.project.model.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  @Query(
      """
      SELECT p
      FROM Project p
      WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.handle) LIKE LOWER(CONCAT('%', :query, '%'))
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

  Optional<Project> findByHandle(String handle);

  boolean existsByHandle(String handle);
}
