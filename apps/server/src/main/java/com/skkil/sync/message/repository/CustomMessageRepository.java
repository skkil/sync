package com.skkil.sync.message.repository;

import com.skkil.sync.message.dto.data.ConversationWithUnreadMessageCountData;
import java.util.List;

interface CustomMessageRepository {

  List<ConversationWithUnreadMessageCountData> getUnreadMessagesCount(Long userId);
}
