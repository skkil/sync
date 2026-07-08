package com.skkil.sync.project.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ProjectOwnerCannotBeModifiedException extends SyncException {

  public ProjectOwnerCannotBeModifiedException() {
    super("Project owner cannot be removed from the project.");
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.FORBIDDEN;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.PROJECT_OWNER_CANNOT_BE_MODIFIED;
  }
}
