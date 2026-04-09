package com.skkil.sync.provider.contest.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ContestOccurrenceNotFoundException extends SyncException {

  public ContestOccurrenceNotFoundException(Long occurrenceId) {
    super("Contest occurrence not found with id: " + occurrenceId);
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.CONTEST_OCCURRENCE_NOT_FOUND;
  }
}
