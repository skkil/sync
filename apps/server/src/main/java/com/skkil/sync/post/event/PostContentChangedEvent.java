package com.skkil.sync.post.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PostContentChangedEvent extends ApplicationEvent {

  private final Long postId;
  private final String content;

  public PostContentChangedEvent(Long postId, String content) {
    super(postId);

    this.postId = postId;
    this.content = content;
  }
}
