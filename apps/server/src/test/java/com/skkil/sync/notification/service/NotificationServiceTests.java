package com.skkil.sync.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.exception.NotificationNotFoundException;
import com.skkil.sync.notification.model.Notification;
import com.skkil.sync.notification.repository.NotificationRepository;
import com.skkil.sync.user.model.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTests {

  @Mock private NotificationRepository notificationRepository;

  @Test
  @DisplayName("[markAsRead] 본인 알림을 읽음 처리")
  void markAsRead_ownedNotification_markAsRead() {
    NotificationService notificationService = new NotificationService(notificationRepository);
    Notification notification =
        Notification.builder().user(new User(1L)).type(NotificationType.USER_FOLLOWED).build();
    notification.setId(10L);

    when(notificationRepository.findByIdAndUser_Id(10L, 1L)).thenReturn(Optional.of(notification));

    notificationService.markAsRead(1L, 10L);

    assertThat(notification.getStatus()).isEqualTo(NotificationStatus.READ);
  }

  @Test
  @DisplayName("[markAsRead] 본인 알림이 아니면 예외 발생")
  void markAsRead_notOwnedNotification_throwNotificationNotFoundException() {
    NotificationService notificationService = new NotificationService(notificationRepository);

    when(notificationRepository.findByIdAndUser_Id(10L, 1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> notificationService.markAsRead(1L, 10L))
        .isInstanceOf(NotificationNotFoundException.class);
  }

  @Test
  @DisplayName("[markAllAsRead] 안 읽은 알림 전체 읽음 처리")
  void markAllAsRead_updateUnreadNotifications() {
    NotificationService notificationService = new NotificationService(notificationRepository);

    notificationService.markAllAsRead(1L);

    verify(notificationRepository)
        .updateStatusByUser(eq(1L), eq(NotificationStatus.UNREAD), eq(NotificationStatus.READ));
  }
}
