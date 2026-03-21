package com.skkil.sync.message.dto.data;

public record ConversationWithUnreadMessageCountData(
    Long conversationId, Long unreadMessageCount) {}
