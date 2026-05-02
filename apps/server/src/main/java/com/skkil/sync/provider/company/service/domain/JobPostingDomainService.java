package com.skkil.sync.provider.company.service.domain;

import com.skkil.sync.provider.company.exception.JobPostingNotFoundException;
import com.skkil.sync.provider.company.model.JobPosting;
import com.skkil.sync.provider.company.repository.JobPostingRepository;
import org.springframework.stereotype.Service;

@Service
public class JobPostingDomainService {

  private final JobPostingRepository jobPostingRepository;

  public JobPostingDomainService(JobPostingRepository jobPostingRepository) {
    this.jobPostingRepository = jobPostingRepository;
  }

  public JobPosting findByIdAndCompanyId(Long jobPostingId, Long companyId) {
    return jobPostingRepository
        .findByIdAndCompanyId(jobPostingId, companyId)
        .orElseThrow(() -> new JobPostingNotFoundException(jobPostingId));
  }
}
