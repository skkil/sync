package com.skkil.sync.post.event;

import java.time.LocalDate;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PostPublishedEvent extends ApplicationEvent {

  private final Long postId;
  private final Long authorId;
  private final LocalDate publishedDate;

  public PostPublishedEvent(Long postId, Long authorId, LocalDate publishedDate) {
    super(postId);

    this.postId = postId;
    this.authorId = authorId;
    this.publishedDate = publishedDate;
  }
}
