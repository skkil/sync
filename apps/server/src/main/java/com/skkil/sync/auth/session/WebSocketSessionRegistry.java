package com.skkil.sync.auth.session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * principal 이름별로 살아 있는 WebSocket 세션을 추적한다.
 *
 * <p>STOMP 세션의 Principal은 핸드셰이크 시점에 고정되고 이후 재검증되지 않으므로, 세션 저장소의 행을 지우는 것({@link
 * SessionInvalidationService})만으로는 이미 열린 소켓이 끊기지 않는다. 프레임워크가 이 연결 고리를 제공하지 않는다 — {@code
 * SimpUserRegistry}는 {@code WebSocketSession} 참조를 들고 있지 않고, spring-session의 {@code
 * WebSocketRegistryListener}는 JDBC 세션 저장소가 발행하지 않는 {@code SessionDestroyedEvent}를 기다린다. 그래서 전송 계층
 * 데코레이터({@link PrincipalTrackingHandshakeDecorator})가 직접 채운다.
 *
 * <p>키는 principal 이름(= 이메일, {@code AuthenticatedUser.getName()})이다. 세션 무효화가 principal 단위로 동작하므로 같은
 * 키를 쓴다 — HTTP 세션 ID로 잡으면 "비밀번호 변경 = 모든 기기 로그아웃" 보장이 기기 하나로 좁아진다.
 *
 * <p>인메모리이므로 다중 인스턴스에서는 자기 인스턴스의 소켓만 끊는다. 현재 배포는 단일 인스턴스이고, 스케일아웃은 인메모리 STOMP 브로커와 같은 지점에서 함께 깨지므로
 * 외부 브로커 도입 시 이 레지스트리도 같이 다뤄야 한다.
 */
@Component
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class WebSocketSessionRegistry {

  /** 로그아웃·타임아웃이 아니라 자격 증명 무효화로 서버가 끊었음을 클라이언트에 알린다. */
  private static final CloseStatus CREDENTIALS_INVALIDATED =
      new CloseStatus(
          CloseStatus.POLICY_VIOLATION.getCode(), "Credentials for this session were invalidated");

  private final Map<String, Map<String, WebSocketSession>> sessionsByPrincipal =
      new ConcurrentHashMap<>();

  void register(String principalName, WebSocketSession session) {
    sessionsByPrincipal
        .computeIfAbsent(principalName, key -> new ConcurrentHashMap<>())
        .put(session.getId(), session);
  }

  void unregister(String principalName, String webSocketSessionId) {
    Map<String, WebSocketSession> sessions = sessionsByPrincipal.get(principalName);
    if (sessions == null) {
      return;
    }

    sessions.remove(webSocketSessionId);
    if (sessions.isEmpty()) {
      // 같은 맵일 때만 제거해, 방금 새 세션을 등록한 동시 연결을 지우지 않는다.
      sessionsByPrincipal.remove(principalName, sessions);
    }
  }

  /** 해당 principal의 소켓을 전부 닫는다. 개별 종료 실패는 나머지 종료를 막지 않는다. */
  public void closeAll(String principalName) {
    Map<String, WebSocketSession> sessions = sessionsByPrincipal.remove(principalName);
    if (sessions == null) {
      return;
    }

    log.info("Closing {} WebSocket session(s) for invalidated principal", sessions.size());
    for (WebSocketSession session : sessions.values()) {
      try {
        session.close(CREDENTIALS_INVALIDATED);
      } catch (IOException exception) {
        // 이미 끊긴 소켓일 가능성이 높다. 서버측 자원은 컨테이너가 정리한다.
        log.debug("WebSocket 세션 {} 종료 실패", session.getId(), exception);
      }
    }
  }

  /** 테스트 전용 — 레지스트리 누수 검증용. */
  boolean hasSessions(String principalName) {
    return sessionsByPrincipal.containsKey(principalName);
  }
}
