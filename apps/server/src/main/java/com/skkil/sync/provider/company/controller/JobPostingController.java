package com.skkil.sync.provider.company.controller;

import com.skkil.sync.common.util.pagination.dto.request.PaginationRequest;
import com.skkil.sync.provider.company.dto.request.CreateJobPostingRequest;
import com.skkil.sync.provider.company.dto.response.CreateJobPostingResponse;
import com.skkil.sync.provider.company.dto.response.GetJobPostingResponse;
import com.skkil.sync.provider.company.dto.response.GetJobPostingsResponse;
import com.skkil.sync.provider.company.service.JobPostingService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobPostingController {

  private final JobPostingService jobPostingService;

  public JobPostingController(JobPostingService jobPostingService) {
    this.jobPostingService = jobPostingService;
  }

  @PostMapping("/companies/{companyId}/jobs")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateJobPostingResponse createJobPosting(
      @PathVariable Long companyId, @RequestBody @Validated CreateJobPostingRequest request) {
    return jobPostingService.createJobPosting(companyId, request);
  }

  @GetMapping("/companies/{companyId}/jobs/{postingId}")
  @ResponseStatus(HttpStatus.OK)
  public GetJobPostingResponse getJobPosting(
      @PathVariable Long companyId, @PathVariable Long postingId) {
    return jobPostingService.getJobPosting(companyId, postingId);
  }

  @GetMapping("/jobs")
  @ResponseStatus(HttpStatus.OK)
  public GetJobPostingsResponse getJobPostings(@Validated PaginationRequest pagination) {
    return jobPostingService.getJobPostings(pagination);
  }

  @GetMapping("/companies/{companyId}/jobs")
  @ResponseStatus(HttpStatus.OK)
  public GetJobPostingsResponse getJobPostingsByCompany(
      @PathVariable Long companyId, @Validated PaginationRequest pagination) {
    return jobPostingService.getJobPostingsByCompany(companyId, pagination);
  }
}
