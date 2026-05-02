package com.skkil.sync.recruitment.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class JobApplicationNotFoundException extends SyncException {

  public JobApplicationNotFoundException(Long id) {
    super("Job application with id " + id + " not found.");
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.JOB_APPLICATION_NOT_FOUND;
  }
}
