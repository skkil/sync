package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class TagNotFoundException extends SyncException {

  public TagNotFoundException(Long tagId) {
    super(String.format("Tag with id %d not found.", tagId));
  }

  public TagNotFoundException(String name) {
    super(String.format("Tag with name %s not found.", name));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.TAG_NOT_FOUND;
  }
}
