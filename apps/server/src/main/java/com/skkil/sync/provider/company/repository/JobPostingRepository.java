package com.skkil.sync.provider.company.repository;

import com.skkil.sync.provider.company.model.Company;
import com.skkil.sync.provider.company.model.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

  @Query("SELECT jp FROM JobPosting jp JOIN FETCH jp.company")
  Page<JobPosting> findJobPostingsWithCompany(Pageable pageable);

  @Query("SELECT jp FROM JobPosting jp WHERE jp.company = :company")
  Page<JobPosting> findByCompany(Company company, Pageable pageable);
}
