package com.skkil.sync.comment.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class InvalidCommentException extends SyncException {

  public InvalidCommentException(String message) {
    super(message);
  }

  public InvalidCommentException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.BAD_REQUEST;
  }
}
