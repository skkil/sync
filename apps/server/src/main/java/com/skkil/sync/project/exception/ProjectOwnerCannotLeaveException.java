package com.skkil.sync.project.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ProjectOwnerCannotLeaveException extends SyncException {

  public ProjectOwnerCannotLeaveException() {
    super("프로젝트 소유자는 프로젝트를 나갈 수 없습니다.");
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.FORBIDDEN;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.PROJECT_OWNER_CANNOT_LEAVE;
  }
}
