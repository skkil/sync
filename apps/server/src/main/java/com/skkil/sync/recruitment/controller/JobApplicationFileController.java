package com.skkil.sync.recruitment.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.recruitment.dto.request.UploadJobApplicationFileRequest;
import com.skkil.sync.recruitment.dto.response.GetJobApplicationFilesResponse;
import com.skkil.sync.recruitment.service.JobApplicationFileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobApplicationFileController {

  private final JobApplicationFileService jobApplicationFileService;

  public JobApplicationFileController(JobApplicationFileService jobApplicationFileService) {
    this.jobApplicationFileService = jobApplicationFileService;
  }

  @PostMapping("/applications/{applicationId}/files")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void uploadJobApplicationFile(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long applicationId,
      @RequestBody @Validated UploadJobApplicationFileRequest request) {
    jobApplicationFileService.uploadJobApplicationFile(user.userId(), applicationId, request);
  }

  @GetMapping("/applications/{applicationId}/files")
  @ResponseStatus(HttpStatus.OK)
  public GetJobApplicationFilesResponse getJobApplicationFiles(@PathVariable Long applicationId) {
    return jobApplicationFileService.getJobApplicationFiles(applicationId);
  }
}
