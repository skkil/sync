package com.skkil.sync.media.exception;

import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class MediaNotFoundException extends SyncException {

  public MediaNotFoundException(Long mediaId) {
    super(String.format("Media with id %d not found.", mediaId));
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }
}
