package com.skkil.sync.notification.event;

import com.skkil.sync.notification.constant.NotificationType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {

  private final Long recipientId;
  private final NotificationType notificationType;

  public NotificationEvent(Long recipientId, NotificationType notificationType) {
    super(recipientId);
    this.recipientId = recipientId;
    this.notificationType = notificationType;
  }
}
