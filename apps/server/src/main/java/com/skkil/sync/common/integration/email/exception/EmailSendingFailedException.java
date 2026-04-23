package com.skkil.sync.common.integration.email.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class EmailSendingFailedException extends SyncException {

  public EmailSendingFailedException(Throwable cause) {
    super("Failed to send email", cause);
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.EMAIL_SENDING_FAILED;
  }
}
