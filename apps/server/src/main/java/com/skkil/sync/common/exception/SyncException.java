package com.skkil.sync.common.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public abstract class SyncException extends RuntimeException implements ErrorResponse {

  public SyncException(String message) {
    super(message);
  }

  @Override
  public ProblemDetail getBody() {
    return ProblemDetail.forStatusAndDetail(getStatusCode(), getMessage());
  }
}
