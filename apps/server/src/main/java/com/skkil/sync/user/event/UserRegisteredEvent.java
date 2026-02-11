package com.skkil.sync.user.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {

  private final Long userId;

  public UserRegisteredEvent(Long userId) {
    super(userId);
    this.userId = userId;
  }
}
