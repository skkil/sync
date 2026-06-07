package com.skkil.sync.user.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserFollowedEvent extends ApplicationEvent {

  private final Long followerId;
  private final Long followeeId;

  public UserFollowedEvent(Long followerId, Long followeeId) {
    super(followerId);
    this.followerId = followerId;
    this.followeeId = followeeId;
  }
}
