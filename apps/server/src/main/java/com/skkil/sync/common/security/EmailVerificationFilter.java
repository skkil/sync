package com.skkil.sync.common.security;

import com.skkil.sync.auth.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class EmailVerificationFilter extends OncePerRequestFilter {

  private static final List<String> EXCLUDED_PATHS =
      List.of(
          "/auth/login",
          "/auth/register",
          "/auth/verify-email",
          "/auth/oauth2",
          "/profiles/",
          "/providers/",
          "/search/",
          "/experiences/");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestUri = request.getRequestURI();

    if (isExcludedPath(requestUri)) {
      filterChain.doFilter(request, response);
      return;
    }

    if ("GET".equalsIgnoreCase(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null
        && authentication.isAuthenticated()
        && authentication.getPrincipal() instanceof AuthenticatedUser user) {

      if (!user.isEmailVerified()) {
        log.warn(
            "User {} attempted to access {} without email verification", user.userId(), requestUri);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"이메일 인증이 필요합니다.\"}");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private boolean isExcludedPath(String requestUri) {
    return EXCLUDED_PATHS.stream().anyMatch(requestUri::startsWith);
  }
}
