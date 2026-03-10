package com.skkil.sync.provider.controller;

import com.skkil.sync.provider.dto.request.CreateJobPostingRequest;
import com.skkil.sync.provider.dto.response.CreateJobPostingResponse;
import com.skkil.sync.provider.dto.response.GetJobPostingsResponse;
import com.skkil.sync.provider.service.company.JobPostingService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {

  private final JobPostingService jobPostingService;

  public CompanyController(JobPostingService jobPostingService) {
    this.jobPostingService = jobPostingService;
  }

  @PostMapping("/companies/{companyId}/jobs")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateJobPostingResponse createJobPosting(
      @PathVariable Long companyId, @RequestBody @Validated CreateJobPostingRequest request) {
    return jobPostingService.createJobPosting(companyId, request);
  }

  @GetMapping("/companies/{companyId}/jobs")
  @ResponseStatus(HttpStatus.OK)
  public GetJobPostingsResponse getJobPostings(@PathVariable Long companyId) {
    return jobPostingService.getJobPostings(companyId);
  }
}
