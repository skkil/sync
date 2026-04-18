package com.skkil.sync.provider.company.service;

import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.provider.company.dto.request.CreateJobPostingRequest;
import com.skkil.sync.provider.company.dto.response.CreateJobPostingResponse;
import com.skkil.sync.provider.company.dto.response.GetJobPostingResponse;
import com.skkil.sync.provider.company.dto.response.GetJobPostingsResponse;
import com.skkil.sync.provider.company.exception.JobPostingNotFoundException;
import com.skkil.sync.provider.company.mapper.CompanyMapper;
import com.skkil.sync.provider.company.model.Company;
import com.skkil.sync.provider.company.model.JobPosting;
import com.skkil.sync.provider.company.repository.CompanyRepository;
import com.skkil.sync.provider.company.repository.JobPostingRepository;
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
  private final PaginationService paginationService;

  public JobPostingService(
      CompanyRepository companyRepository,
      JobPostingRepository jobPostingRepository,
      CompanyMapper companyMapper,
      PaginationService paginationService) {
    this.companyRepository = companyRepository;
    this.jobPostingRepository = jobPostingRepository;
    this.companyMapper = companyMapper;
    this.paginationService = paginationService;
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
  public GetJobPostingResponse getJobPosting(Long companyId, Long postingId) {
    JobPosting jobPosting =
        jobPostingRepository
            .findByIdAndCompanyId(postingId, companyId)
            .orElseThrow(() -> new JobPostingNotFoundException(postingId));

    return companyMapper.toGetJobPostingResponse(jobPosting);
  }

  @Transactional(readOnly = true)
  public GetJobPostingsResponse getJobPostings(OffsetPaginationRequest pagination) {
    var postings =
        paginationService
            .paginate(
                (pageable) -> jobPostingRepository.findJobPostingsWithCompany(pageable), pagination)
            .map(companyMapper::toGetJobPostingsResponseJobPosting);

    return new GetJobPostingsResponse(postings);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#companyId, 'PROVIDER', 'READ')")
  public GetJobPostingsResponse getJobPostingsByCompany(
      Long companyId, OffsetPaginationRequest pagination) {
    Company company = companyRepository.getReferenceById(companyId);

    var postings =
        paginationService
            .paginate(pageable -> jobPostingRepository.findByCompany(company, pageable), pagination)
            .map(companyMapper::toGetJobPostingsResponseJobPosting);

    return new GetJobPostingsResponse(postings);
  }
}
