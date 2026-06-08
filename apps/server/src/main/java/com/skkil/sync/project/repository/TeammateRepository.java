package com.skkil.sync.project.repository;

import com.skkil.sync.project.model.Teammate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeammateRepository extends JpaRepository<Teammate, Long> {

  boolean existsByProjectIdAndUserIdAndIsOwnerTrue(Long projectId, Long userId);
}
