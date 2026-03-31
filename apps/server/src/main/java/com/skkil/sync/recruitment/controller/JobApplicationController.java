package com.skkil.sync.recruitment.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.PaginationRequest;
import com.skkil.sync.recruitment.dto.request.CreateJobApplicationRequest;
import com.skkil.sync.recruitment.dto.response.CreateJobApplicationResponse;
import com.skkil.sync.recruitment.dto.response.GetJobApplicationsResponse;
import com.skkil.sync.recruitment.service.JobApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobApplicationController {

  private final JobApplicationService jobApplicationService;

  public JobApplicationController(JobApplicationService jobApplicationService) {
    this.jobApplicationService = jobApplicationService;
  }

  @PostMapping("/applications")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateJobApplicationResponse createJobApplication(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody @Validated CreateJobApplicationRequest request) {
    return jobApplicationService.createJobApplication(user.userId(), request);
  }

  @GetMapping("/applications/me")
  @ResponseStatus(HttpStatus.OK)
  public GetJobApplicationsResponse getMyJobApplications(
      @AuthenticationPrincipal AuthenticatedUser user, @Validated PaginationRequest pagination) {
    return jobApplicationService.getJobApplications(user.userId(), pagination);
  }
}
