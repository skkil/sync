package com.skkil.sync.user.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class UserCannotRemoveOAuthAccountException extends SyncException {

  public UserCannotRemoveOAuthAccountException(String message) {
    super(message);
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.CONFLICT;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.OAUTH2_ACCOUNT_CANNOT_BE_DELETED;
  }
}
