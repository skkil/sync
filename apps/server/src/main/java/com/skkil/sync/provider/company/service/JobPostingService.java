package com.skkil.sync.provider.company.service;

import com.skkil.sync.provider.company.dto.request.CreateJobPostingRequest;
import com.skkil.sync.provider.company.dto.response.CreateJobPostingResponse;
import com.skkil.sync.provider.company.dto.response.GetJobPostingsResponse;
import com.skkil.sync.provider.company.mapper.CompanyMapper;
import com.skkil.sync.provider.company.model.Company;
import com.skkil.sync.provider.company.model.JobPosting;
import com.skkil.sync.provider.company.repository.CompanyRepository;
import com.skkil.sync.provider.company.repository.JobPostingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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

    return new CreateJobPostingResponse(jobPosting.getId().toString());
  }

  @Transactional(readOnly = true)
  public GetJobPostingsResponse getJobPostings(Integer page, Integer size) {
    var postings =
        jobPostingRepository
            .findJobPostingsWithCompany(PageRequest.of(page, size))
            .map(companyMapper::toJobPostingResponse);

    log.debug("Retrieved {} job postings", postings.getNumberOfElements());
    return new GetJobPostingsResponse(postings);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#companyId, 'PROVIDER', 'READ')")
  public GetJobPostingsResponse getJobPostingsByCompany(
      Long companyId, Integer page, Integer size) {
    Company company = companyRepository.getReferenceById(companyId);

    var postings =
        jobPostingRepository
            .findByCompany(company, PageRequest.of(page, size))
            .map(companyMapper::toJobPostingResponse);

    log.debug(
        "Retrieved {} job postings for company {}", postings.getNumberOfElements(), companyId);
    return new GetJobPostingsResponse(postings);
  }
}
