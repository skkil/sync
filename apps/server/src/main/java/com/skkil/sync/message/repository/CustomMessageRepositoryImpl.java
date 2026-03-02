package com.skkil.sync.message.repository;

import static com.skkil.sync.jooq.tables.Conversations.CONVERSATIONS;
import static com.skkil.sync.jooq.tables.Messages.MESSAGES;
import static com.skkil.sync.jooq.tables.Participants.PARTICIPANTS;
import static org.jooq.impl.DSL.count;

import com.skkil.sync.message.dto.data.ConversationWithUnreadMessageCountData;
import java.util.List;
import org.jooq.DSLContext;

class CustomMessageRepositoryImpl implements CustomMessageRepository {

  private final DSLContext dsl;

  public CustomMessageRepositoryImpl(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public List<ConversationWithUnreadMessageCountData> getUnreadMessagesCount(Long userId) {
    return dsl.select(CONVERSATIONS.ID, count())
        .from(CONVERSATIONS)
        .join(MESSAGES)
        .on(CONVERSATIONS.ID.eq(MESSAGES.CONVERSATION_ID))
        .join(PARTICIPANTS)
        .on(CONVERSATIONS.ID.eq(PARTICIPANTS.CONVERSATION_ID))
        .where(
            PARTICIPANTS
                .USER_ID
                .eq(userId)
                .and(PARTICIPANTS.LAST_READ_AT.lessThan(MESSAGES.CREATED_AT))
                .or(PARTICIPANTS.LAST_READ_AT.isNull()))
        .groupBy(CONVERSATIONS.ID)
        .having(count().gt(0))
        .fetchInto(ConversationWithUnreadMessageCountData.class);
  }
}
