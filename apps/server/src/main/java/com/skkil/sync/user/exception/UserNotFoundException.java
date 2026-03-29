package com.skkil.sync.user.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class UserNotFoundException extends SyncException {

  public UserNotFoundException(Long userId) {
    super(String.format("User with id %d not found.", userId));
  }

  public UserNotFoundException(String handle) {
    super(String.format("User with handle '%s' not found.", handle));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.USER_NOT_FOUND;
  }
}
