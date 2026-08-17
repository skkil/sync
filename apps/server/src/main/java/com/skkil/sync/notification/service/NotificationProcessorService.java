package com.skkil.sync.notification.service;

import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.notification.channel.NotificationChannel;
import com.skkil.sync.notification.constant.ChannelType;
import com.skkil.sync.notification.dto.data.NotificationSummary;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.notification.mapper.NotificationMapper;
import com.skkil.sync.notification.model.Notification;
import com.skkil.sync.notification.repository.NotificationRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.jspecify.annotations.Nullable;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Slf4j
public class NotificationProcessorService {

  private final NotificationPreferencesService notificationPreferencesService;
  private final NotificationRepository notificationRepository;
  private final UserDomainService userDomainService;
  private final NotificationMapper notificationMapper;
  private final MediaDomainService mediaDomainService;
  private final Map<ChannelType, NotificationChannel> channels;

  public NotificationProcessorService(
      NotificationPreferencesService notificationPreferencesService,
      NotificationRepository notificationRepository,
      UserDomainService userDomainService,
      NotificationMapper notificationMapper,
      MediaDomainService mediaDomainService,
      List<NotificationChannel> channels) {
    this.notificationPreferencesService = notificationPreferencesService;
    this.notificationRepository = notificationRepository;
    this.userDomainService = userDomainService;
    this.notificationMapper = notificationMapper;
    this.mediaDomainService = mediaDomainService;
    this.channels =
        channels.stream().collect(Collectors.toMap(NotificationChannel::type, Function.identity()));
  }

  /**
   * 저장은 이 트랜잭션에서 확정하고, 푸시는 커밋 이후에 best-effort로 보낸다.
   *
   * <p>커밋 전에 푸시하면 두 가지가 깨진다. 하나는 순서 — 아직 커밋되지 않은 알림을 클라이언트가 받아 되조회하면 없다. 다른 하나가 더 무겁다: 푸시 경로(DTO
   * 조립·S3 서명·직렬화)에서 난 예외가 아무에게도 잡히지 않으면 이미 INSERT된 알림까지 롤백되어 영구 소실된다 — 재발행 경로가 없다. 그렇다고 트랜잭션 안에서
   * try/catch로 감싸면, 참여 중인 {@code @Transactional} 빈이 던진 예외가 공유 트랜잭션을 rollback-only로 표시해 커밋이 {@code
   * UnexpectedRollbackException}으로 끝난다 — 표시는 호출부 catch로 지울 수 없다. 커밋 뒤로 옮기면 표시할 트랜잭션 자체가 없으므로 두 경로가
   * 함께 닫힌다.
   */
  @Async
  @EventListener
  @Transactional
  public void handleNotificationEvent(NotificationEvent event) {
    log.debug("Processing notification event: {}", event);

    if (!notificationPreferencesService.isInAppEnabled(event.getRecipientId())) {
      log.debug("Notifications are disabled for user: {}", event.getRecipientId());
      return;
    }

    Notification notification = createNotification(event);

    NotificationChannel channel = channels.get(ChannelType.IN_APP);
    if (channel == null) {
      log.debug(
          "No channel available for type {}; notification {} is stored but not pushed.",
          ChannelType.IN_APP,
          notification.getId());
      return;
    }

    // afterCommit 시점에도 EntityManager는 열려 있지만(정리는 그 다음 단계),
    // 그때의 지연 로딩은 끝난 트랜잭션 밖에서 커넥션을 새로 잡는다. 푸시에 필요한
    // 연관은 트랜잭션 안에서 미리 초기화해 콜백을 DB-free로 만든다.
    warmUpForPush(notification.getUser(), notification.getActor());

    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              push(channel, notification);
            }
          });
    } else {
      // 트랜잭션 밖 호출(단위 테스트 등) — 지킬 커밋이 없으므로 그 자리에서 푸시한다.
      push(channel, notification);
    }
  }

  private void push(NotificationChannel channel, Notification notification) {
    try {
      log.debug("Sending notification {} via channel {}", notification.getId(), ChannelType.IN_APP);
      channel.send(notification.getUser(), toDto(notification));
    } catch (RuntimeException exception) {
      // 알림은 이미 커밋됐다. 푸시만 실패한 것이므로 수신자는 목록 조회로 볼 수 있다.
      log.warn(
          "알림 {} 는 저장됐지만 실시간 푸시에 실패했다 — 수신자는 목록 조회로 확인할 수 있다.", notification.getId(), exception);
    }
  }

  private void warmUpForPush(User recipient, @Nullable User actor) {
    Hibernate.initialize(recipient);
    if (actor != null) {
      Hibernate.initialize(actor);
      Hibernate.initialize(actor.getProfileImage());
    }
  }

  private Notification createNotification(NotificationEvent event) {
    User user = userDomainService.getUserReference(event.getRecipientId());
    User actor =
        event.getActorId() == null ? null : userDomainService.getUserReference(event.getActorId());

    Notification notification =
        Notification.builder()
            .user(user)
            .actor(actor)
            .type(event.getNotificationType())
            .entityType(event.getEntityType())
            .entityId(event.getEntityId())
            .payload(notificationMapper.toPayloadJson(event.getPayload()))
            .build();
    return notificationRepository.save(notification);
  }

  private NotificationSummary toDto(Notification notification) {
    return notificationMapper.toDto(notification, resolveActorProfileImageUrl(notification));
  }

  private Map<Long, URL> resolveActorProfileImageUrl(Notification notification) {
    @Nullable User actor = notification.getActor();
    if (actor == null) {
      return Map.of();
    }

    // 아바타 URL은 있으면 좋은 장식이다 — 서명 실패가 푸시 자체를 막지 않도록
    // 관대한 변형을 쓴다. 매퍼는 URL이 없으면 null로 내려보낸다.
    return mediaDomainService.generatePresignedGetUrlsLenient(
        List.of(actor), User::getProfileImage);
  }
}
