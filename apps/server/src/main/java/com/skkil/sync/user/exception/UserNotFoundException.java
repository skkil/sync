package com.skkil.sync.user.exception;

import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class UserNotFoundException extends SyncException {

  public UserNotFoundException(Long userId) {
    super(String.format("User with id %d not found.", userId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }
}
