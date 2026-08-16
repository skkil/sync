package com.skkil.sync.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.user.constant.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.messaging.web.csrf.CsrfChannelInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * WebSocket은 나머지 테스트에서 꺼져 있다(application.yaml 기본값 false). 여기서만 켜서 브로커와 보안 설정이 실제로 조립되는지, 그리고 STOMP
 * 프레임 인가 규칙 — 특히 목적지에 사용자 ID를 넣던 과거 방식(/topic/notifications/{id})이 기본 거부로 막히는지 — 를 검증한다.
 */
@Import(TestcontainersConfig.class)
@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {"app.seed.enabled=false", "app.websocket.enabled=true"})
class WebSocketSecurityIntegrationTests {

  @Autowired private AuthorizationManager<Message<?>> messageAuthorizationManager;

  @Autowired
  @Qualifier("csrfChannelInterceptor")
  private ChannelInterceptor csrfChannelInterceptor;

  @Test
  void csrfChannelInterceptor_isPlainComparisonVariant() {
    // 빈 이름 "csrfChannelInterceptor"는 Spring Security가 기본 Xor 인터셉터의 교체
    // 지점으로 조회하는 계약이다. csrf.spa()의 raw 토큰 방식과 맞추려면 평문 비교
    // 인터셉터여야 하며, 이 단언이 깨지면 STOMP CONNECT가 전부 거부된다.
    assertThat(csrfChannelInterceptor).isInstanceOf(CsrfChannelInterceptor.class);
  }

  @Test
  void subscribeToUserQueue_isGrantedForAuthenticatedUser() {
    AuthorizationResult result =
        messageAuthorizationManager.authorize(
            () -> authenticatedUser(1L), subscribeTo("/user/queue/notifications"));

    assertThat(result).isNotNull();
    assertThat(result.isGranted()).isTrue();
  }

  @Test
  void subscribeToLegacyTopicDestination_isDeniedEvenForAuthenticatedUser() {
    // 과거 취약 목적지 — 로그인한 사용자가 남의 알림 토픽을 구독할 수 있었다.
    // 규칙에 없는 목적지이므로 기본 거부에 걸려야 한다.
    AuthorizationResult result =
        messageAuthorizationManager.authorize(
            () -> authenticatedUser(1L), subscribeTo("/topic/notifications/2"));

    assertThat(result).isNotNull();
    assertThat(result.isGranted()).isFalse();
  }

  @Test
  void subscribeToUserQueue_isDeniedForAnonymousUser() {
    Authentication anonymous =
        new AnonymousAuthenticationToken(
            "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    AuthorizationResult result =
        messageAuthorizationManager.authorize(
            () -> anonymous, subscribeTo("/user/queue/notifications"));

    assertThat(result).isNotNull();
    assertThat(result.isGranted()).isFalse();
  }

  @Test
  void clientSendFrame_isDeniedEvenForAuthenticatedUser() {
    // 살아 있는 @MessageMapping이 없으므로 클라이언트 발신 SEND 프레임은 전부 거부한다.
    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
    accessor.setDestination("/app/conversations/send");
    Message<byte[]> send = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    AuthorizationResult result =
        messageAuthorizationManager.authorize(() -> authenticatedUser(1L), send);

    assertThat(result).isNotNull();
    assertThat(result.isGranted()).isFalse();
  }

  @Test
  void connectFrame_isPermittedWithoutAuthentication() {
    // CONNECT 자체는 허용한다 — 인증은 핸드셰이크(HTTP 세션)가, CSRF는 csrfChannelInterceptor가 맡는다.
    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.CONNECT);
    Message<byte[]> connect =
        MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    AuthorizationResult result = messageAuthorizationManager.authorize(() -> null, connect);

    assertThat(result).isNotNull();
    assertThat(result.isGranted()).isTrue();
  }

  private static Message<byte[]> subscribeTo(String destination) {
    SimpMessageHeaderAccessor accessor =
        SimpMessageHeaderAccessor.create(SimpMessageType.SUBSCRIBE);
    accessor.setDestination(destination);
    return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
  }

  private static Authentication authenticatedUser(Long userId) {
    AuthenticatedUser principal =
        AuthenticatedUser.builder()
            .userId(userId)
            .fullName("사용자" + userId)
            .email("user" + userId + "@example.com")
            .role(Role.USER)
            .enabled(true)
            .build();
    return UsernamePasswordAuthenticationToken.authenticated(
        principal, null, principal.getAuthorities());
  }
}
