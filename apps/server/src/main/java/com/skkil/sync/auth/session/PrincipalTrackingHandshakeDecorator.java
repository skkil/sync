package com.skkil.sync.auth.session;

import java.security.Principal;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

/**
 * 전송 계층에서 연결 수립·종료를 가로채 {@link WebSocketSessionRegistry}를 채운다.
 *
 * <p>STOMP 프레임이 아니라 소켓 자체에 걸리므로 CONNECT 프레임·CSRF 검증보다 먼저 동작하고, 구독 여부와 무관하게 모든 인증된 연결을 추적한다. 핸드셰이크는
 * HTTP 필터 체인의 {@code anyRequest().authenticated()}를 통과했으므로 Principal이 존재하며, 그 이름은 이메일이다.
 */
public class PrincipalTrackingHandshakeDecorator extends WebSocketHandlerDecorator {

  private final WebSocketSessionRegistry registry;

  public PrincipalTrackingHandshakeDecorator(
      WebSocketHandler delegate, WebSocketSessionRegistry registry) {
    super(delegate);
    this.registry = registry;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    Principal principal = session.getPrincipal();
    if (principal != null) {
      registry.register(principal.getName(), session);
    }

    super.afterConnectionEstablished(session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
      throws Exception {
    Principal principal = session.getPrincipal();
    if (principal != null) {
      registry.unregister(principal.getName(), session.getId());
    }

    super.afterConnectionClosed(session, closeStatus);
  }
}
