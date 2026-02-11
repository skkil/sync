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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
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
  @TransactionalEventListener
  public void handleNotificationEvent(NotificationEvent event) {
    NotificationPreferences preferences =
        notificationPreferencesService.getNotificationPreferences();
    if (!preferences.isNotificationsEnabled()) {
      return;
    }

    Notification notification =
        Notification.builder().user(new User(event.getRecipientId())).build();
    notification = notificationRepository.save(notification);

    for (ChannelType channelType : preferences.getEnabledChannelTypes()) {
      NotificationChannel channel = channels.get(channelType);
      if (channel == null) {
        throw new IllegalStateException("No channel found for type: " + channelType);
      }

      channel.send(event.getRecipientId(), "Notification " + notification.getId());
    }
  }
}
