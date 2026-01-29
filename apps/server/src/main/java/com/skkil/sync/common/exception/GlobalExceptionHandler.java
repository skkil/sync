package com.skkil.sync.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(SyncException.class)
  public ResponseEntity<ProblemDetail> handle(SyncException e, HttpServletRequest request) {
    log.debug(
        "Received SyncException [{}] for request {}: {}",
        e.getClass().getSimpleName(),
        request.getRequestURI(),
        e.getMessage());

    return ResponseEntity.status(e.getStatusCode()).body(e.getBody());
  }
}
