package com.skkil.sync.provider.company.controller;

import com.skkil.sync.provider.company.dto.request.CreateJobPostingRequest;
import com.skkil.sync.provider.company.dto.response.CreateJobPostingResponse;
import com.skkil.sync.provider.company.dto.response.GetJobPostingsResponse;
import com.skkil.sync.provider.company.service.JobPostingService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/jobs")
  @ResponseStatus(HttpStatus.OK)
  public GetJobPostingsResponse getJobPostings(
      @RequestParam(required = false, defaultValue = "0") Integer page,
      @RequestParam(required = false, defaultValue = "50") Integer size) {
    return jobPostingService.getJobPostings(page, size);
  }

  @GetMapping("/companies/{companyId}/jobs")
  @ResponseStatus(HttpStatus.OK)
  public GetJobPostingsResponse getJobPostingsByCompany(
      @PathVariable Long companyId,
      @RequestParam(required = false, defaultValue = "0") Integer page,
      @RequestParam(required = false, defaultValue = "50") Integer size) {
    return jobPostingService.getJobPostingsByCompany(companyId, page, size);
  }
}
