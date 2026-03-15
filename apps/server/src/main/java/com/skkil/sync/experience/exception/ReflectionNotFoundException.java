package com.skkil.sync.experience.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ReflectionNotFoundException extends SyncException {

  public ReflectionNotFoundException(Long reflectionId) {
    super(String.format("Reflection with id %d not found.", reflectionId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.REFLECTION_NOT_FOUND;
  }
}
