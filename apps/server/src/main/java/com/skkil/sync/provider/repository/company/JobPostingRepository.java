package com.skkil.sync.provider.repository.company;

import com.skkil.sync.provider.model.company.Company;
import com.skkil.sync.provider.model.company.JobPosting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

  @Query("SELECT jp FROM JobPosting jp WHERE jp.company = :company")
  List<JobPosting> findByCompany(Company company);
}
