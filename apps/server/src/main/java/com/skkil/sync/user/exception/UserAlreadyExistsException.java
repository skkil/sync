package com.skkil.sync.user.exception;

import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class UserAlreadyExistsException extends SyncException {

  public UserAlreadyExistsException(String email) {
    super(String.format("User with email %s already exists.", email));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.CONFLICT;
  }
}
