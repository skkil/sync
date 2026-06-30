package com.skkil.sync.project.repository;

import com.skkil.sync.project.model.Teammate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeammateRepository extends JpaRepository<Teammate, Long> {

  @EntityGraph(attributePaths = {"user"})
  List<Teammate> findByProjectId(Long projectId);

  @EntityGraph(attributePaths = {"user"})
  List<Teammate> findByProjectId(Long projectId, Pageable pageable);

  boolean existsByProjectIdAndUserIdAndIsOwnerTrue(Long projectId, Long userId);

  Optional<Teammate> findByProjectIdAndUserId(Long projectId, Long userId);

  @EntityGraph(attributePaths = {"user"})
  Optional<Teammate> findByProjectIdAndUserHandle(Long projectId, String userHandle);

  void deleteByProjectIdAndUserHandle(Long projectId, String userHandle);
}
