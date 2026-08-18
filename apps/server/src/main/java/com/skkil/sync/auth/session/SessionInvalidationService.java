package com.skkil.sync.auth.session;

import com.skkil.sync.auth.agent.AgentAuthorizationRevoker;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

/**
 * 비밀번호가 바뀌었을 때 그 계정으로 발급된 자격 증명을 전부 끊는다.
 *
 * <p>세션만으로는 부족하다. 에이전트 OAuth2 인가는 세션 저장소가 아니라 {@code oauth2_authorization} 에 남고, 갱신 토큰은 60일을 산다.
 * WebSocket 연결은 핸드셰이크 시점의 Principal을 끝까지 유지해 세션 행 삭제로는 끊기지 않는다. 셋을 한자리에서 지워야 "비밀번호를 바꾸면 모든 자격 증명이
 * 무효가 된다"는 규칙이 실제로 성립한다.
 */
@Service
@Slf4j
public class SessionInvalidationService {

  private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;
  private final AgentAuthorizationRevoker agentAuthorizationRevoker;
  private final ObjectProvider<WebSocketSessionRegistry> webSocketSessionRegistry;

  public SessionInvalidationService(
      FindByIndexNameSessionRepository<? extends Session> sessionRepository,
      AgentAuthorizationRevoker agentAuthorizationRevoker,
      ObjectProvider<WebSocketSessionRegistry> webSocketSessionRegistry) {
    this.sessionRepository = sessionRepository;
    this.agentAuthorizationRevoker = agentAuthorizationRevoker;
    this.webSocketSessionRegistry = webSocketSessionRegistry;
  }

  public void invalidateAllSessions(String principalName) {
    Map<String, ? extends Session> sessions = sessionRepository.findByPrincipalName(principalName);
    sessions.keySet().forEach(sessionRepository::deleteById);

    log.info("Invalidated {} session(s)", sessions.size());

    agentAuthorizationRevoker.revokeAll(principalName);
    closeWebSocketSessions(principalName);
  }

  /**
   * 소켓 종료는 즉시 수행한다 — 알림 푸시처럼 커밋 뒤로 미루지 않는다. 실패 방향이 반대이기 때문이다: 트랜잭션이 롤백됐는데 소켓을 끊었다면 클라이언트가 재연결하면
   * 그만이지만, 커밋 후 콜백이 실행되지 않아 소켓이 살아남으면 무효화된 자격 증명이 계속 알림을 받는다. 보안에서는 과하게 끊는 쪽이 안전하다.
   *
   * <p>반대로 종료 실패가 비밀번호 변경·계정 삭제를 되돌려서도 안 되므로 예외는 흡수한다. 레지스트리 빈은 WebSocket이 켜진 구성에만 존재한다 ({@code
   * app.websocket.enabled}).
   */
  private void closeWebSocketSessions(String principalName) {
    try {
      webSocketSessionRegistry.ifAvailable(registry -> registry.closeAll(principalName));
    } catch (RuntimeException exception) {
      log.warn("WebSocket 세션 종료에 실패했다. principal={}", principalName, exception);
    }
  }
}
