package com.skkil.sync.user.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class UserCannotFollowSelfException extends SyncException {

  public UserCannotFollowSelfException() {
    super("User cannot follow themselves");
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.USER_CANNOT_FOLLOW_SELF;
  }
}
