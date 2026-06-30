package com.skkil.sync.comment.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class CommentNotFoundException extends SyncException {

  public CommentNotFoundException(Long commentId) {
    super(String.format("Comment with id %d not found.", commentId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.COMMENT_NOT_FOUND;
  }
}
