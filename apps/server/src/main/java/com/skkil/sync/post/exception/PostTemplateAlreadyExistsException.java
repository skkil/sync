package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PostTemplateAlreadyExistsException extends SyncException {

  public PostTemplateAlreadyExistsException(String name) {
    super(String.format("PostTemplate with name '%s' already exists in this project.", name));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.CONFLICT;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.POST_TEMPLATE_ALREADY_EXISTS;
  }
}
