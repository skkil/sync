package com.skkil.sync.auth.handler;

import com.skkil.sync.common.exception.ErrorCode;
import com.skkil.sync.user.exception.OAuth2AccountCannotBeLinkedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

  @Value("${app.oauth2.frontend-redirect-uri}")
  private String frontendRedirectUri;

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {
    if (exception instanceof OAuth2AccountCannotBeLinkedException) {
      log.debug("OAuth2 account cannot be linked: {}", exception.getMessage());
      response.sendRedirect(
          frontendRedirectUri + "?error=" + ErrorCode.OAUTH2_ACCOUNT_CANNOT_BE_LINKED);
    } else {
      log.debug("Authentication failed: {}", exception.getMessage());
      response.sendRedirect(frontendRedirectUri);
    }
  }
}
