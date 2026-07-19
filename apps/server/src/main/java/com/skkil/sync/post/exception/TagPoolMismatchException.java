package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class TagPoolMismatchException extends SyncException {

  public TagPoolMismatchException(Long sourceTagId, Long targetTagId) {
    super(
        String.format(
            "Tags %d and %d are not in the same pool and cannot be merged.",
            sourceTagId, targetTagId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.TAG_POOL_MISMATCH;
  }
}
