package com.skkil.sync.experience.exception;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ProviderNotVerifiedException extends SyncException {

  public ProviderNotVerifiedException(Long providerId) {
    super(String.format("Provider with id %d is not verified.", providerId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public ErrorCode getErrorCode() {
    return ErrorCode.PROVIDER_NOT_VERIFIED;
  }
}
