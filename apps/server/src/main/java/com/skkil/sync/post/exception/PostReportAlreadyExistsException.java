package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PostReportAlreadyExistsException extends SyncException {

  public PostReportAlreadyExistsException(Long postId, Long reporterId) {
    super(message(postId, reporterId));
  }

  public PostReportAlreadyExistsException(Long postId, Long reporterId, Throwable cause) {
    super(message(postId, reporterId), cause);
  }

  private static String message(Long postId, Long reporterId) {
    return String.format("User %d already reported post %d.", reporterId, postId);
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.CONFLICT;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.POST_REPORT_ALREADY_EXISTS;
  }
}
