package com.skkil.sync.message.repository;

import com.skkil.sync.message.dto.data.ConversationData;
import java.util.List;

interface CustomConversationRepository {

  List<ConversationData> getConversationsForUser(Long userId);

  boolean existsByConversationIdAndUserId(Long conversationId, Long userId);
}
