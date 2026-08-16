package com.skkil.sync.notification.channel;

import com.skkil.sync.notification.constant.ChannelType;
import com.skkil.sync.notification.dto.data.NotificationSummary;
import com.skkil.sync.user.model.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.websocket.enabled", havingValue = "true", matchIfMissing = false)
public class InAppNotificationChannel implements NotificationChannel {

  private static final String NOTIFICATIONS_DESTINATION = "/queue/notifications";

  private final SimpMessagingTemplate messagingTemplate;

  public InAppNotificationChannel(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @Override
  public ChannelType type() {
    return ChannelType.IN_APP;
  }

  /**
   * 사용자 목적지로 발행한다 — 클라이언트는 {@code /user/queue/notifications}를 구독하고, 브로커가 세션별 목적지로 해소하므로 목적지 문자열에
   * 수신자 식별자가 노출되지 않는다. 첫 인자는 STOMP 세션 Principal 이름과 일치해야 하며, 이 앱의 세션 Principal 이름은 이메일이다 ({@code
   * AuthenticatedUser.getName()} — 세션 무효화가 이메일로 조회하는 것과 같은 계약).
   */
  @Override
  public void send(User recipient, NotificationSummary notification) {
    messagingTemplate.convertAndSendToUser(
        recipient.getEmail(), NOTIFICATIONS_DESTINATION, notification);
  }
}
