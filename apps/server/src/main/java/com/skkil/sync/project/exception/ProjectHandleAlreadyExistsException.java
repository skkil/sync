package com.skkil.sync.project.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ProjectHandleAlreadyExistsException extends SyncException {

  public ProjectHandleAlreadyExistsException() {
    super("A project with this handle already exists.");
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.CONFLICT;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.PROJECT_HANDLE_ALREADY_EXISTS;
  }
}
