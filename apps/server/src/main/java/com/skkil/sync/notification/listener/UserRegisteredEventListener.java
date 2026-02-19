package com.skkil.sync.notification.listener;

import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.user.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class UserRegisteredEventListener {

  private final ApplicationEventPublisher eventPublisher;

  public UserRegisteredEventListener(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Async
  @TransactionalEventListener
  public void handleUserRegisteredEvent(UserRegisteredEvent event) {
    log.debug("User registered event received for user ID: {}", event.getUserId());
    eventPublisher.publishEvent(new NotificationEvent(event.getUserId(), NotificationType.WELCOME));
  }
}
