package com.skkil.sync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OAuthSecurityConfig {

  @Value("${app.oauth2.frontend-redirect-uri}")
  private String frontendRedirectUri;

  private final OidcUserService oidcUserService;

  public OAuthSecurityConfig(OidcUserService oidcUserService) {
    this.oidcUserService = oidcUserService;
  }

  @Bean
  @Order(1)
  SecurityFilterChain oAuthSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher("/oauth2/**", "/login/oauth2/**")
        .oauth2Login(
            oauth2 ->
                oauth2
                    .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService))
                    .successHandler(
                        (req, res, auth) -> {
                          res.sendRedirect(frontendRedirectUri);
                        }))
        .build();
  }
}
