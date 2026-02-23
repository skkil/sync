package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.constant.OAuth2Provider;
import com.skkil.sync.user.dto.response.GetOAuth2AccountsResponse;
import com.skkil.sync.user.service.oauth2.OAuth2Service;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2Controller {

  private final OAuth2Service oAuth2Service;

  public OAuth2Controller(OAuth2Service oAuth2Service) {
    this.oAuth2Service = oAuth2Service;
  }

  @GetMapping("/users/me/oauth2")
  @ResponseStatus(HttpStatus.OK)
  public GetOAuth2AccountsResponse getOAuth2Accounts(
      @AuthenticationPrincipal AuthenticatedUser user) {
    return oAuth2Service.getOAuth2Accounts(user.userId());
  }

  @DeleteMapping("/users/me/oauth2/{provider}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteOAuth2Account(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
      @PathVariable OAuth2Provider provider) {
    oAuth2Service.deleteOAuth2Account(authenticatedUser.userId(), provider);
  }
}
