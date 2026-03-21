package com.skkil.sync.common.socket.handler;

import com.skkil.sync.auth.AuthenticatedUser;
import java.security.Principal;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
@Slf4j
public class WebSocketHandshakeHandler extends DefaultHandshakeHandler {

  @Override
  protected @Nullable Principal determineUser(
      ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
    Principal principal = request.getPrincipal();

    if (principal != null && principal instanceof Authentication authentication) {
      AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
      log.debug("Found authenticated user {} for WebSocket connection", user.userId());
      return user;
    }

    return super.determineUser(request, wsHandler, attributes);
  }
}
