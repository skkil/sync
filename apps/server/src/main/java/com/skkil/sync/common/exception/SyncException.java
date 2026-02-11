package com.skkil.sync.common.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public abstract class SyncException extends RuntimeException implements ErrorResponse {

  public SyncException(String message) {
    super(message);
  }

  public abstract ErrorCode getErrorCode();

  @Override
  public ProblemDetail getBody() {
    ProblemDetail detail = ProblemDetail.forStatusAndDetail(getStatusCode(), getMessage());
    detail.setProperty("code", getErrorCode());
    return detail;
  }
}
