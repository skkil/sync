package com.skkil.sync.message.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record GetMessagesResponse(Page<Message> messages) {

  @Builder
  public static record Message(
      String messageId, String senderId, String content, LocalDateTime sentAt) {}
}
