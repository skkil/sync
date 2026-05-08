package com.skkil.sync.auth.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class LoginRequiredException extends SyncException {

  public LoginRequiredException() {
    super("로그인이 필요한 기능입니다.");
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.LOGIN_REQUIRED;
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.UNAUTHORIZED;
  }
}
