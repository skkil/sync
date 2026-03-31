package com.skkil.sync.provider.company.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class JobPostingNotFoundException extends SyncException {

  public JobPostingNotFoundException(Long jobPostingId) {
    super(String.format("Job Posting with id %d not found.", jobPostingId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.JOB_POSTING_NOT_FOUND;
  }
}
