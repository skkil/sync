package com.skkil.sync.post.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PostLikedEvent extends ApplicationEvent {

  private final Long postId;
  private final Long postAuthorId;
  private final Long likerId;

  public PostLikedEvent(Long postId, Long postAuthorId, Long likerId) {
    super(postId);

    this.postId = postId;
    this.postAuthorId = postAuthorId;
    this.likerId = likerId;
  }
}
