package com.skkil.sync.notification.listener;

import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.user.event.UserRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventListener {

  private final ApplicationEventPublisher eventPublisher;

  public UserRegisteredEventListener(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Async
  @EventListener
  public void handleUserRegisteredEvent(UserRegisteredEvent event) {
    eventPublisher.publishEvent(new NotificationEvent(event.getUserId(), NotificationType.WELCOME));
  }
}
