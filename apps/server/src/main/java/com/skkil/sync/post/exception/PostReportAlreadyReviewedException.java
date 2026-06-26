package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PostReportAlreadyReviewedException extends SyncException {

  public PostReportAlreadyReviewedException(Long reportId) {
    super(String.format("Post report %d has already been reviewed.", reportId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.CONFLICT;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.POST_REPORT_ALREADY_REVIEWED;
  }
}
