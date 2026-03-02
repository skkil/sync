package com.skkil.sync.config;

import com.skkil.sync.auth.handler.OAuth2AuthenticationFailureHandler;
import com.skkil.sync.auth.handler.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OAuth2SecurityConfig {

  private final OidcUserService oidcUserService;
  private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
  private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

  public OAuth2SecurityConfig(
      OidcUserService oidcUserService,
      OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
      OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
    this.oidcUserService = oidcUserService;
    this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
    this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
  }

  @Bean
  @Order(1)
  SecurityFilterChain oAuthSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher("/oauth2/**", "/login/oauth2/**")
        .oauth2Login(
            oauth2 ->
                oauth2
                    .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService))
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler))
        .build();
  }
}
