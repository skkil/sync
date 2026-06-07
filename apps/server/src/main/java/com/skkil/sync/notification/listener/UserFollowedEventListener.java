package com.skkil.sync.notification.listener;

import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.user.event.UserFollowedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class UserFollowedEventListener {

  private final ApplicationEventPublisher eventPublisher;

  public UserFollowedEventListener(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Async
  @TransactionalEventListener
  public void handleUserFollowedEvent(UserFollowedEvent event) {
    log.debug(
        "User followed event received: follower ID={}, followee ID={}",
        event.getFollowerId(),
        event.getFolloweeId());
    eventPublisher.publishEvent(
        new NotificationEvent(
            event.getFolloweeId(), NotificationType.USER_FOLLOWED, event.getFollowerId()));
  }
}
