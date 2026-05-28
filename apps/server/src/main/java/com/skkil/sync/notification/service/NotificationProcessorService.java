package com.skkil.sync.notification.service;

import com.skkil.sync.notification.channel.NotificationChannel;
import com.skkil.sync.notification.constant.ChannelType;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.notification.model.Notification;
import com.skkil.sync.notification.model.NotificationPreferences;
import com.skkil.sync.notification.repository.NotificationRepository;
import com.skkil.sync.user.model.User;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class NotificationProcessorService {

  private final NotificationPreferencesService notificationPreferencesService;
  private final NotificationRepository notificationRepository;
  private final Map<ChannelType, NotificationChannel> channels;

  public NotificationProcessorService(
      NotificationPreferencesService notificationPreferencesService,
      NotificationRepository notificationRepository,
      List<NotificationChannel> channels) {
    this.notificationPreferencesService = notificationPreferencesService;
    this.notificationRepository = notificationRepository;
    this.channels =
        channels.stream().collect(Collectors.toMap(NotificationChannel::type, Function.identity()));
  }

  @Async
  @EventListener
  public void handleNotificationEvent(NotificationEvent event) {
    log.debug("Processing notification event: {}", event);

    NotificationPreferences preferences =
        notificationPreferencesService.getNotificationPreferences();
    if (!preferences.isNotificationsEnabled()) {
      log.debug("Notifications are disabled for user: {}", event.getRecipientId());
      return;
    }

    Notification notification = createNotification(event);

    for (ChannelType channelType : preferences.getEnabledChannelTypes()) {
      NotificationChannel channel = channels.get(channelType);
      if (channel == null) {
        throw new IllegalStateException("No channel found for type: " + channelType);
      }

      log.debug("Sending notification {} via channel {}", notification.getId(), channelType);
      channel.send(event.getRecipientId(), "Notification " + notification.getId());
    }
  }

  @Transactional
  Notification createNotification(NotificationEvent event) {
    User actor = event.getActorId() == null ? null : new User(event.getActorId());
    Notification notification =
        Notification.builder()
            .user(new User(event.getRecipientId()))
            .actor(actor)
            .type(event.getNotificationType())
            .build();
    return notificationRepository.save(notification);
  }
}
