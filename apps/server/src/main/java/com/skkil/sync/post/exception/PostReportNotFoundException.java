package com.skkil.sync.post.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PostReportNotFoundException extends SyncException {

  public PostReportNotFoundException(Long reportId) {
    super(String.format("Post report %d not found.", reportId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.POST_REPORT_NOT_FOUND;
  }
}
