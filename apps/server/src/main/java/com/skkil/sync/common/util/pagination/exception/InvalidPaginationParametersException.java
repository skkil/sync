package com.skkil.sync.common.util.pagination.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class InvalidPaginationParametersException extends SyncException {

  public InvalidPaginationParametersException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.BAD_REQUEST;
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.BAD_REQUEST;
  }
}
