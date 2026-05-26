package com.skkil.sync.reflection.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import com.skkil.sync.reflection.constants.ReflectionConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ReflectionTagLimitExceededException extends SyncException {

  public ReflectionTagLimitExceededException() {
    super(
        String.format(
            "A reflection cannot have more than %d tags",
            ReflectionConstants.MAX_TAGS_PER_REFLECTION));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.CONFLICT;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.TAG_LIMIT_EXCEEDED;
  }
}
