package com.skkil.sync.provider.repository;

import com.skkil.sync.provider.model.Lab;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabRepository extends JpaRepository<Lab, Long> {

  Page<Lab> findByResearchAreaContainingIgnoreCase(String keyword, Pageable pageable);

  Page<Lab> findByProfessorId(String professorId, Pageable pageable);
}
