package com.skkil.sync.notification.event;

import com.skkil.sync.notification.constant.NotificationType;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {

  private final Long recipientId;
  private final NotificationType notificationType;
  private final @Nullable Long actorId;

  public NotificationEvent(Long recipientId, NotificationType notificationType) {
    this(recipientId, notificationType, null);
  }

  public NotificationEvent(
      Long recipientId, NotificationType notificationType, @Nullable Long actorId) {
    super(recipientId);
    this.recipientId = recipientId;
    this.notificationType = notificationType;
    this.actorId = actorId;
  }
}
