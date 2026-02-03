package com.skkil.sync.lab.repository;

import com.skkil.sync.lab.model.Lab;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabRepository extends JpaRepository<Lab, Long> {

  @Query(
      "SELECT l FROM Lab l WHERE "
          + "LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(l.researchArea) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<Lab> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

  @Query("SELECT l FROM Lab l WHERE l.professor.id = :professorId")
  Page<Lab> findByProfessorId(@Param("professorId") Long professorId, Pageable pageable);
}
