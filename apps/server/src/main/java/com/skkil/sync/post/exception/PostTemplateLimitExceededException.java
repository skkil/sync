package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import com.skkil.sync.post.constants.PostConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PostTemplateLimitExceededException extends SyncException {

  public PostTemplateLimitExceededException() {
    super(
        String.format(
            "A project cannot have more than %d post templates",
            PostConstants.MAX_POST_TEMPLATES_PER_PROJECT));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.CONFLICT;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.POST_TEMPLATE_LIMIT_EXCEEDED;
  }
}
