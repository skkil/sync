package com.skkil.sync.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.repository.MediaRepository;
import com.skkil.sync.notification.channel.NotificationChannel;
import com.skkil.sync.notification.constant.ChannelType;
import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.dto.data.NotificationSummary;
import com.skkil.sync.notification.dto.response.GetNotificationsResponse;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.notification.model.NewFollowerPayload;
import com.skkil.sync.notification.repository.NotificationRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import io.awspring.cloud.s3.S3Template;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * 알림 저장과 실시간 푸시의 순서·격리를 실제 트랜잭션과 실제 빈 위에서 검증한다.
 *
 * <p>저장은 커밋으로 확정되고 푸시는 그 이후의 best-effort여야 한다 — S3 서명이나 채널 전송이 실패해도 알림 행은 살아남아야 한다. 과거 구조(커밋 전 푸시,
 * 단일 트랜잭션)에서는 서명·전송 예외가 INSERT까지 함께 롤백시켜 이 테스트들이 실패한다.
 *
 * <p>{@code MediaDomainService}는 모킹하지 않는다 — 서명 실패는 그 아래의 {@link S3Template}에서 일으켜, 관대한 서명 경로가 실제
 * 코드로 실행되게 한다.
 */
@Import({
  TestcontainersConfig.class,
  NotificationPushResilienceIntegrationTests.ChannelConfig.class
})
@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {"app.seed.enabled=false", "app.websocket.enabled=false"})
class NotificationPushResilienceIntegrationTests {

  @Autowired private ApplicationEventPublisher eventPublisher;
  @Autowired private NotificationRepository notificationRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private MediaRepository mediaRepository;
  @Autowired private NotificationService notificationService;
  @Autowired private RecordingChannel recordingChannel;

  @MockitoBean private S3Template s3Template;

  private User recipient;
  private User actor;

  @BeforeEach
  void setUp() {
    recordingChannel.reset();
    recipient = saveUser("push-recipient");
    actor = saveUser("push-actor");
  }

  @Test
  @DisplayName("S3 서명이 실패해도 알림 행은 커밋되고 푸시는 URL 없이 나간다")
  void presignFailure_notificationPersistedAndPushedWithoutUrl() {
    givenActorAvatar();
    when(s3Template.createSignedGetURL(anyString(), anyString(), any(Duration.class)))
        .thenThrow(new IllegalStateException("IMDS 자격증명 해소 실패 시뮬레이션"));

    eventPublisher.publishEvent(newFollowerEvent());

    awaitUnreadCount(1);
    await("푸시 도착", () -> pushesToRecipient() == 1);

    // 읽기 경로도 같은 장애에서 살아남아야 한다 — 아바타 한 건의 서명 실패가
    // 알림 목록 전체를 실패시키면 안 된다.
    GetNotificationsResponse response =
        notificationService.getNotifications(recipient.getId(), new OffsetPaginationRequest(0, 20));
    assertThat(response.notifications().content()).hasSize(1);
  }

  @Test
  @DisplayName("실시간 푸시가 실패해도 알림 행은 커밋된다")
  void pushFailure_notificationStillPersisted() {
    recordingChannel.failNextSend();

    eventPublisher.publishEvent(newFollowerEvent());

    awaitUnreadCount(1);
    await("푸시 시도", () -> pushesToRecipient() == 1);
  }

  @Test
  @DisplayName("정상 경로에서는 저장 후 수신자에게 푸시된다")
  void happyPath_persistsThenPushes() {
    eventPublisher.publishEvent(newFollowerEvent());

    awaitUnreadCount(1);
    await("푸시 도착", () -> pushesToRecipient() == 1);
  }

  private void givenActorAvatar() {
    Media avatar =
        Media.builder()
            .uploader(actor)
            .mediaType("image/png")
            .bucket("test-bucket")
            .key("avatar-" + System.nanoTime())
            .fileName("avatar.png")
            .fileSize(1024L)
            .build();
    avatar.markAsUploaded();
    mediaRepository.save(avatar);
    actor.setProfileImage(avatar);
    actor = userRepository.save(actor);
  }

  private NotificationEvent newFollowerEvent() {
    return new NotificationEvent(
        recipient.getId(),
        NotificationType.NEW_FOLLOWER,
        actor.getId(),
        null,
        null,
        new NewFollowerPayload("follower", "팔로워"));
  }

  private User saveUser(String prefix) {
    return userRepository.save(
        User.builder()
            .email(prefix + "-" + System.nanoTime() + "@example.com")
            .fullName("알림 테스트")
            .hashedPassword("hash")
            .build());
  }

  /** 이전 테스트의 비동기 잔류 푸시가 섞이지 않도록, 이 테스트의 수신자 분만 센다. */
  private long pushesToRecipient() {
    return recordingChannel.recipientEmails().stream()
        .filter(email -> email.equals(recipient.getEmail()))
        .count();
  }

  private void awaitUnreadCount(long expected) {
    await(
        "알림 저장",
        () ->
            notificationRepository.countByUser_IdAndStatus(
                    recipient.getId(), NotificationStatus.UNREAD)
                == expected);
  }

  /** 비동기({@code @Async}) 처리 완료를 폴링으로 기다린다 (Awaitility 미도입). */
  private static void await(String what, BooleanSupplier condition) {
    Instant deadline = Instant.now().plusSeconds(10);
    while (Instant.now().isBefore(deadline)) {
      if (condition.getAsBoolean()) {
        return;
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException interrupted) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(interrupted);
      }
    }
    throw new AssertionError(what + " 대기가 10초 안에 끝나지 않았다");
  }

  @TestConfiguration
  static class ChannelConfig {

    /** WebSocket 플래그와 무관하게 IN_APP 채널을 공급해 커밋-후-푸시 경로를 실행시킨다. */
    @Bean
    RecordingChannel recordingChannel() {
      return new RecordingChannel();
    }
  }

  static class RecordingChannel implements NotificationChannel {

    private final AtomicBoolean failNextSend = new AtomicBoolean();
    private final List<String> recipientEmails = new CopyOnWriteArrayList<>();

    @Override
    public ChannelType type() {
      return ChannelType.IN_APP;
    }

    @Override
    public void send(User recipient, NotificationSummary notification) {
      recipientEmails.add(recipient.getEmail());
      if (failNextSend.getAndSet(false)) {
        throw new IllegalStateException("브로커 전송 실패 시뮬레이션");
      }
    }

    void failNextSend() {
      failNextSend.set(true);
    }

    List<String> recipientEmails() {
      return List.copyOf(recipientEmails);
    }

    void reset() {
      failNextSend.set(false);
      recipientEmails.clear();
    }
  }
}
