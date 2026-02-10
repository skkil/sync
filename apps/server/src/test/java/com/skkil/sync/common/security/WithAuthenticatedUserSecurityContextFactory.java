package com.skkil.sync.common.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.constant.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAuthenticatedUserSecurityContextFactory
    implements WithSecurityContextFactory<WithAuthenticatedUser> {

  @Override
  public SecurityContext createSecurityContext(WithAuthenticatedUser annotation) {
    AuthenticatedUser authenticatedUser =
        new AuthenticatedUser(1L, "User Name", "email@email.com", "password", Role.USER);

    Authentication authentication =
        UsernamePasswordAuthenticationToken.authenticated(authenticatedUser, null, null);

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);

    return context;
  }
}
