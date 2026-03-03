package com.skkil.sync.user.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2AccountCannotBeLinkedException extends OAuth2AuthenticationException {

  public OAuth2AccountCannotBeLinkedException(String message) {
    super(message);
  }
}
