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
      WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
      LIMIT 10
      """)
  List<Project> searchProjects(String query);

  List<Project> findByTeammatesUserIdAndNameContainingIgnoreCase(Long userId, String query);

  Optional<Project> findByHandle(String handle);

  boolean existsByHandle(String handle);
}
