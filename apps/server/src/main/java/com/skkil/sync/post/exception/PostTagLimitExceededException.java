package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import com.skkil.sync.post.constants.PostConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PostTagLimitExceededException extends SyncException {

  public PostTagLimitExceededException() {
    super(String.format("A post cannot have more than %d tags", PostConstants.MAX_TAGS_PER_POST));
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
