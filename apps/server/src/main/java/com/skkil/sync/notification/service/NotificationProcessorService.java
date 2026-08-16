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
import org.jspecify.annotations.Nullable;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    log.debug("Sending notification {} via channel {}", notification.getId(), ChannelType.IN_APP);
    channel.send(notification.getUser(), toDto(notification));
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

    return mediaDomainService.generatePresignedGetUrls(List.of(actor), User::getProfileImage);
  }
}
