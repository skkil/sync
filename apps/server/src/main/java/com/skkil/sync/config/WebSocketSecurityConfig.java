package com.skkil.sync.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.web.csrf.CsrfChannelInterceptor;

/**
 * STOMP 프레임 레벨 인가. HTTP 필터 체인은 핸드셰이크까지만 보호하고, 이후 SUBSCRIBE/SEND 프레임은 clientInboundChannel을 타므로 여기서
 * 별도로 인가해야 한다. 규칙에 없는 목적지는 전부 거부하는 기본 거부 정책이라, 새 목적지를 열려면 이 파일에 명시적으로 추가해야 한다.
 */
@Configuration
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true", matchIfMissing = false)
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

  @Bean
  AuthorizationManager<Message<?>> messageAuthorizationManager(
      MessageMatcherDelegatingAuthorizationManager.Builder messages) {
    return messages
        .simpTypeMatchers(
            SimpMessageType.CONNECT,
            SimpMessageType.DISCONNECT,
            SimpMessageType.UNSUBSCRIBE,
            SimpMessageType.HEARTBEAT)
        .permitAll()
        // 사용자 목적지는 브로커가 세션별로 해소하므로, 인증만 요구하면 각자 자기
        // 알림만 받는다. 목적지에 사용자 식별자를 넣는 방식(/topic/notifications/{id})은
        // 로그인한 아무나 남의 알림을 구독할 수 있어 금지 — enableSimpleBroker에서
        // /topic을 열지 않는 것도 같은 이유다.
        .simpSubscribeDestMatchers("/user/queue/notifications")
        .authenticated()
        .anyMessage()
        .denyAll()
        .build();
  }

  /**
   * 기본 {@link org.springframework.security.messaging.web.csrf.XorCsrfChannelInterceptor}는 XOR 마스킹된
   * 토큰만 받는데, 이 앱의 CSRF 설정({@code csrf.spa()})은 클라이언트가 {@code XSRF-TOKEN} 쿠키의 원본 값을 그대로 헤더에 싣는 SPA
   * 방식이다. HTTP 경로의 {@code SpaCsrfTokenRequestHandler}가 원본 토큰을 받는 것과 동일하게, STOMP CONNECT도 평문 비교
   * 인터셉터로 맞춘다. 빈 이름은 Spring Security가 교체 지점으로 조회하는 계약이므로 바꾸면 안 된다.
   */
  @Bean(name = "csrfChannelInterceptor")
  ChannelInterceptor csrfChannelInterceptor() {
    return new CsrfChannelInterceptor();
  }
}
