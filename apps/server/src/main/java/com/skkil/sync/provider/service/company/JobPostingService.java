package com.skkil.sync.provider.service.company;

import com.skkil.sync.provider.dto.request.CreateJobPostingRequest;
import com.skkil.sync.provider.dto.response.CreateJobPostingResponse;
import com.skkil.sync.provider.dto.response.GetJobPostingsResponse;
import com.skkil.sync.provider.mapper.CompanyMapper;
import com.skkil.sync.provider.model.company.Company;
import com.skkil.sync.provider.model.company.JobPosting;
import com.skkil.sync.provider.repository.company.CompanyRepository;
import com.skkil.sync.provider.repository.company.JobPostingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class JobPostingService {

  private final CompanyRepository companyRepository;
  private final JobPostingRepository jobPostingRepository;
  private final CompanyMapper companyMapper;

  public JobPostingService(
      CompanyRepository companyRepository,
      JobPostingRepository jobPostingRepository,
      CompanyMapper companyMapper) {
    this.companyRepository = companyRepository;
    this.jobPostingRepository = jobPostingRepository;
    this.companyMapper = companyMapper;
  }

  @Transactional
  @PreAuthorize("hasPermission(#companyId, 'PROVIDER', 'EDIT')")
  public CreateJobPostingResponse createJobPosting(
      Long companyId, CreateJobPostingRequest request) {
    Company company = companyRepository.getReferenceById(companyId);

    JobPosting jobPosting =
        JobPosting.builder()
            .company(company)
            .jobTitle(request.jobTitle())
            .jobDescription(request.jobDescription())
            .location(request.location())
            .build();

    jobPosting = jobPostingRepository.save(jobPosting);
    log.debug("Created job posting with id {} for company {}", jobPosting.getId(), companyId);

    return new CreateJobPostingResponse(jobPosting.getId());
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#companyId, 'PROVIDER', 'READ')")
  public GetJobPostingsResponse getJobPostings(Long companyId) {
    Company company = companyRepository.getReferenceById(companyId);

    var postings =
        jobPostingRepository.findByCompany(company).stream()
            .map(companyMapper::toJobPostingResponse)
            .toList();

    log.debug("Retrieved {} job postings for company {}", postings.size(), companyId);
    return new GetJobPostingsResponse(postings);
  }
}
