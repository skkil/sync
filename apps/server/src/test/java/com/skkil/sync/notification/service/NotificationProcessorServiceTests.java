package com.skkil.sync.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.notification.model.Notification;
import com.skkil.sync.notification.repository.NotificationRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationProcessorServiceTests {

  @Mock private NotificationPreferencesService notificationPreferencesService;
  @Mock private NotificationRepository notificationRepository;

  @Test
  @DisplayName("[createNotification] actor가 있는 알림 저장")
  void createNotification_withActor_saveNotificationWithActor() {
    NotificationProcessorService notificationProcessorService =
        new NotificationProcessorService(
            notificationPreferencesService, notificationRepository, List.of());
    when(notificationRepository.save(any(Notification.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Notification notification =
        notificationProcessorService.createNotification(
            new NotificationEvent(2L, NotificationType.USER_FOLLOWED, 1L));

    assertThat(notification.getUser().getId()).isEqualTo(2L);
    assertThat(notification.getActor()).isNotNull();
    assertThat(notification.getActor().getId()).isEqualTo(1L);
    assertThat(notification.getType()).isEqualTo(NotificationType.USER_FOLLOWED);
  }

  @Test
  @DisplayName("[createNotification] actor가 없는 알림 저장")
  void createNotification_withoutActor_saveNotificationWithoutActor() {
    NotificationProcessorService notificationProcessorService =
        new NotificationProcessorService(
            notificationPreferencesService, notificationRepository, List.of());
    when(notificationRepository.save(any(Notification.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Notification notification =
        notificationProcessorService.createNotification(
            new NotificationEvent(2L, NotificationType.WELCOME));

    assertThat(notification.getUser().getId()).isEqualTo(2L);
    assertThat(notification.getActor()).isNull();
    assertThat(notification.getType()).isEqualTo(NotificationType.WELCOME);
  }
}
