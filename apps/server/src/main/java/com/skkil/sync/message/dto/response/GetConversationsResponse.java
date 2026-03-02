package com.skkil.sync.message.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public record GetConversationsResponse(List<Conversation> conversations) {

  @Builder
  public static record Conversation(
      String conversationId, List<Participant> participants, Message lastMessage) {}

  @Builder
  public static record Participant(String userId, String name, String profileImageUrl) {}

  @Builder
  public static record Message(
      String messageId, String senderId, String content, LocalDateTime sentAt) {}
}
