package com.skkil.sync.notification.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.user.event.UserFollowedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class UserFollowedEventListenerTests {

  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private UserFollowedEventListener userFollowedEventListener;

  @Test
  @DisplayName("[handleUserFollowedEvent] 팔로우 이벤트를 팔로우 알림 이벤트로 변환")
  void handleUserFollowedEvent_publishNotificationEvent() {
    UserFollowedEvent userFollowedEvent = new UserFollowedEvent(1L, 2L);

    userFollowedEventListener.handleUserFollowedEvent(userFollowedEvent);

    ArgumentCaptor<NotificationEvent> eventCaptor =
        ArgumentCaptor.forClass(NotificationEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());

    NotificationEvent event = eventCaptor.getValue();
    assertThat(event.getRecipientId()).isEqualTo(2L);
    assertThat(event.getActorId()).isEqualTo(1L);
    assertThat(event.getNotificationType()).isEqualTo(NotificationType.USER_FOLLOWED);
  }
}
