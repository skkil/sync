package com.skkil.sync.experience.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ExperienceNotFoundException extends SyncException {

  public ExperienceNotFoundException(Long experienceId) {
    super(String.format("Experience with id %d not found.", experienceId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.EXPERIENCE_NOT_FOUND;
  }
}
