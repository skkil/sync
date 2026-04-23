package com.skkil.sync.user.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class EmailVerificationTokenExpiredException extends SyncException {

  public EmailVerificationTokenExpiredException() {
    super("Email verification token has expired.");
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN;
  }
}
