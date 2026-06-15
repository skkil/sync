package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PostNotFoundException extends SyncException {

  public PostNotFoundException(Long postId) {
    super(String.format("Post with id %d not found.", postId));
  }

  public PostNotFoundException(String slug) {
    super(String.format("Post with slug '%s' not found.", slug));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.POST_NOT_FOUND;
  }
}
