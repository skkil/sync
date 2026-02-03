package com.skkil.sync.message.exception;

import com.skkil.sync.common.exception.SyncException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class MessageToSelfException extends SyncException {

  public MessageToSelfException() {
    super("Cannot send message to oneself.");
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.BAD_REQUEST;
  }
}
