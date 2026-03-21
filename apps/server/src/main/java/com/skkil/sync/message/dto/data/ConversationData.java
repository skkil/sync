package com.skkil.sync.message.dto.data;

import java.util.List;

public record ConversationData(
    Long conversationId, List<ParticipantData> participants, MessageData lastMessage) {}
