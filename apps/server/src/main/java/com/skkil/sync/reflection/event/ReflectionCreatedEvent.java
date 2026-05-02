package com.skkil.sync.reflection.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReflectionCreatedEvent extends ApplicationEvent {

  private final Long reflectionId;
  private final String content;

  public ReflectionCreatedEvent(Long reflectionId, String content) {
    super(reflectionId);

    this.reflectionId = reflectionId;
    this.content = content;
  }
}
