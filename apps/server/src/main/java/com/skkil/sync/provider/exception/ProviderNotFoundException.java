package com.skkil.sync.provider.exception;

import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ProviderNotFoundException extends SyncException {

  public ProviderNotFoundException(Long providerId) {
    super(String.format("Provider with id %d not found.", providerId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }
}
