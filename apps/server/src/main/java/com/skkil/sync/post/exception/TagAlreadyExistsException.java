package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class TagAlreadyExistsException extends SyncException {

  public TagAlreadyExistsException(String name) {
    super(String.format("Tag with name %s already exists.", name));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.CONFLICT;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.TAG_ALREADY_EXISTS;
  }
}
