package com.skkil.sync.notification.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.notification.channel.NotificationChannel;
import com.skkil.sync.notification.constant.ChannelType;
import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.dto.data.NotificationSummary;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.notification.mapper.NotificationMapper;
import com.skkil.sync.notification.model.NewFollowerPayload;
import com.skkil.sync.notification.model.Notification;
import com.skkil.sync.notification.repository.NotificationRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationProcessorServiceTests {

  @Mock private NotificationPreferencesService notificationPreferencesService;
  @Mock private NotificationRepository notificationRepository;
  @Mock private UserDomainService userDomainService;
  @Mock private NotificationMapper notificationMapper;
  @Mock private MediaDomainService mediaDomainService;

  /** 프로덕션은 WebSocket이 꺼져 있어 채널 빈이 하나도 없다. 그 상태에서도 알림은 저장돼야 한다 — 이 강등 경로가 프로덕션이 실제로 쓰는 유일한 경로다. */
  @Test
  void handleNotificationEvent_withoutAnyChannel_stillPersistsNotification() {
    NotificationProcessorService service = serviceWithChannels(List.of());
    stubRecipient();

    service.handleNotificationEvent(newFollowerEventTo(1L));

    verify(notificationRepository).save(any(Notification.class));
  }

  @Test
  void handleNotificationEvent_withInAppChannel_pushesToRecipient() {
    NotificationChannel inAppChannel = mock(NotificationChannel.class);
    when(inAppChannel.type()).thenReturn(ChannelType.IN_APP);
    NotificationProcessorService service = serviceWithChannels(List.of(inAppChannel));
    User recipient = stubRecipient();
    NotificationSummary summary =
        new NotificationSummary(
            1L,
            NotificationType.NEW_FOLLOWER,
            NotificationStatus.UNREAD,
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            null,
            null,
            new NewFollowerPayload("follower", "팔로워"));
    when(notificationMapper.toDto(any(Notification.class), anyMap())).thenReturn(summary);

    service.handleNotificationEvent(newFollowerEventTo(1L));

    // 채널이 받는 수신자는 저장된 알림의 사용자여야 한다 — 사용자 목적지의
    // 키(이메일)가 여기서 나오므로, 다른 User가 넘어가면 알림이 엉뚱한 사람에게 간다.
    verify(inAppChannel).send(same(recipient), eq(summary));
  }

  /** 소실 회귀: DTO 조립(presign) 실패가 저장을 되돌리거나 호출자에게 전파되면 안 된다. 푸시는 best-effort이고, 알림 저장이 항상 우선한다. */
  @Test
  void handleNotificationEvent_presignFailure_keepsSaveAndDoesNotPropagate() {
    NotificationChannel inAppChannel = mock(NotificationChannel.class);
    when(inAppChannel.type()).thenReturn(ChannelType.IN_APP);
    NotificationProcessorService service = serviceWithChannels(List.of(inAppChannel));
    stubRecipient();
    User actor =
        User.builder().email("actor@example.com").fullName("행위자").hashedPassword("hash").build();
    when(userDomainService.getUserReference(2L)).thenReturn(actor);
    when(mediaDomainService.generatePresignedGetUrlsLenient(anyList(), any()))
        .thenThrow(new IllegalStateException("S3 자격증명 해소 실패"));

    NotificationEvent event =
        new NotificationEvent(
            1L,
            NotificationType.NEW_FOLLOWER,
            2L,
            null,
            null,
            new NewFollowerPayload("follower", "팔로워"));

    assertThatCode(() -> service.handleNotificationEvent(event)).doesNotThrowAnyException();

    verify(notificationRepository).save(any(Notification.class));
    verify(inAppChannel, never()).send(any(), any());
  }

  @Test
  void handleNotificationEvent_notificationsDisabled_skipsSaveAndPush() {
    NotificationChannel inAppChannel = mock(NotificationChannel.class);
    when(inAppChannel.type()).thenReturn(ChannelType.IN_APP);
    NotificationProcessorService service = serviceWithChannels(List.of(inAppChannel));
    when(notificationPreferencesService.isInAppEnabled(1L)).thenReturn(false);

    service.handleNotificationEvent(newFollowerEventTo(1L));

    verify(notificationRepository, never()).save(any(Notification.class));
    verify(inAppChannel, never()).send(any(), any());
  }

  private NotificationProcessorService serviceWithChannels(List<NotificationChannel> channels) {
    return new NotificationProcessorService(
        notificationPreferencesService,
        notificationRepository,
        userDomainService,
        notificationMapper,
        mediaDomainService,
        channels);
  }

  private User stubRecipient() {
    User recipient =
        User.builder()
            .email("recipient@example.com")
            .fullName("수신자")
            .hashedPassword("hash")
            .build();
    when(notificationPreferencesService.isInAppEnabled(1L)).thenReturn(true);
    when(userDomainService.getUserReference(1L)).thenReturn(recipient);
    when(notificationMapper.toPayloadJson(any())).thenReturn(JsonNodeFactory.instance.objectNode());
    when(notificationRepository.save(any(Notification.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    return recipient;
  }

  private static NotificationEvent newFollowerEventTo(Long recipientId) {
    return new NotificationEvent(
        recipientId,
        NotificationType.NEW_FOLLOWER,
        null,
        null,
        null,
        new NewFollowerPayload("follower", "팔로워"));
  }
}
