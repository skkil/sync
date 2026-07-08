package com.skkil.sync.common.devtools.seed;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Some service methods are guarded by {@code @PreAuthorize("hasPermission(...)")}, which resolves
 * the acting user from the security context. Seeding runs outside of any HTTP request, so this
 * temporarily installs a principal for the duration of the action.
 */
final class SeedSecurityContext {

  private SeedSecurityContext() {}

  static void runAs(User user, Runnable action) {
    AuthenticatedUser principal =
        new AuthenticatedUser(
            user.getId(), user.getFullName(), user.getEmail(), null, user.getRole());
    Authentication authentication =
        UsernamePasswordAuthenticationToken.authenticated(
            principal, null, principal.getAuthorities());

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    try {
      action.run();
    } finally {
      SecurityContextHolder.clearContext();
    }
  }
}
