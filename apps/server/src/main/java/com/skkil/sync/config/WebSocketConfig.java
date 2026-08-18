package com.skkil.sync.config;

import com.skkil.sync.auth.session.PrincipalTrackingHandshakeDecorator;
import com.skkil.sync.auth.session.WebSocketSessionRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true", matchIfMissing = false)
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${app.cors.allowed-origins}")
  private String[] allowedOrigins;

  private final WebSocketSessionRegistry sessionRegistry;

  public WebSocketConfig(WebSocketSessionRegistry sessionRegistry) {
    this.sessionRegistry = sessionRegistry;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
    // 브로드캐스트 네임스페이스(/topic)는 의도적으로 열지 않는다. 유일한 실시간
    // 소비자인 알림이 사용자 목적지(/user/queue/**)로만 발행되므로, 목적지에
    // 사용자 식별자를 넣고 아무나 구독하는 실수를 구조적으로 막는다.
    registry.enableSimpleBroker("/queue");
    registry.setPreservePublishOrder(true);
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOrigins(allowedOrigins).withSockJS();
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    // 세션 무효화(비밀번호 변경·계정 삭제)가 살아 있는 소켓까지 끊을 수 있도록,
    // 모든 연결을 principal 이름으로 레지스트리에 등록한다.
    registration.addDecoratorFactory(
        handler -> new PrincipalTrackingHandshakeDecorator(handler, sessionRegistry));
  }
}
