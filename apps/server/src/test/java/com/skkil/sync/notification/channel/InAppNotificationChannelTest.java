package com.skkil.sync.notification.channel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.dto.data.NotificationSummary;
import com.skkil.sync.notification.model.NewFollowerPayload;
import com.skkil.sync.user.model.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class InAppNotificationChannelTest {

  @Test
  void send_publishesToUserDestinationKeyedByPrincipalName() {
    SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
    InAppNotificationChannel channel = new InAppNotificationChannel(messagingTemplate);
    User recipient =
        User.builder()
            .email("recipient@example.com")
            .fullName("수신자")
            .hashedPassword("hash")
            .build();
    NotificationSummary notification =
        new NotificationSummary(
            1L,
            NotificationType.NEW_FOLLOWER,
            NotificationStatus.UNREAD,
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            null,
            null,
            new NewFollowerPayload("follower", "팔로워"));

    channel.send(recipient, notification);

    // 목적지에 수신자 식별자가 들어가지 않고, 사용자 목적지의 키는 세션 Principal
    // 이름(이메일)과 일치해야 한다. 둘 중 하나라도 어긋나면 알림이 조용히 유실된다.
    // 목적지 문자열은 웹 클라이언트가 구독하는 /user/queue/notifications와의 계약이므로
    // 프로덕션 상수를 재사용하지 않고 리터럴로 단언한다.
    verify(messagingTemplate)
        .convertAndSendToUser("recipient@example.com", "/queue/notifications", notification);
  }
}
