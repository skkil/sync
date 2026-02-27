package com.skkil.sync.config;

import com.skkil.sync.common.socket.handler.WebSocketHandshakeHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${app.cors.allowed-origins}")
  private String[] allowedOrigins;

  private final WebSocketHandshakeHandler webSocketHandshakeHandler;

  public WebSocketConfig(WebSocketHandshakeHandler webSocketHandshakeHandler) {
    this.webSocketHandshakeHandler = webSocketHandshakeHandler;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setUserDestinationPrefix("/user");
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/queue");
    registry.setPreservePublishOrder(true);
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws")
        .setAllowedOrigins(allowedOrigins)
        .setHandshakeHandler(webSocketHandshakeHandler)
        .addInterceptors(new HttpSessionHandshakeInterceptor())
        .withSockJS()
        .setSessionCookieNeeded(true);
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    registry.setMessageSizeLimit(8192); // max 8KB per message
    registry.setSendBufferSizeLimit(256 * 1024); // total send buffer size
    registry.setSendTimeLimit(10000); // 10 seconds max per send
  }
}
