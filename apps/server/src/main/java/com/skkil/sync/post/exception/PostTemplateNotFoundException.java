package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PostTemplateNotFoundException extends SyncException {

  public PostTemplateNotFoundException(String externalId) {
    super(String.format("PostTemplate with external id '%s' not found.", externalId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.POST_TEMPLATE_NOT_FOUND;
  }
}
