package com.skkil.sync.message.dto.response;

import java.util.List;

public record GetUnreadMessagesCountResponse(List<Conversation> conversations) {

  public static record Conversation(String conversationId, Long unreadCount) {}
}
