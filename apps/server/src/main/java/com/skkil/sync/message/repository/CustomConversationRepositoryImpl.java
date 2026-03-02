package com.skkil.sync.message.repository;

import static com.skkil.sync.jooq.tables.Conversations.CONVERSATIONS;
import static com.skkil.sync.jooq.tables.Messages.MESSAGES;
import static com.skkil.sync.jooq.tables.Participants.PARTICIPANTS;
import static com.skkil.sync.jooq.tables.Users.USERS;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.select;

import com.skkil.sync.message.dto.data.ConversationData;
import com.skkil.sync.message.dto.data.MessageData;
import com.skkil.sync.message.dto.data.ParticipantData;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Records;

class CustomConversationRepositoryImpl implements CustomConversationRepository {

  private final DSLContext dsl;

  public CustomConversationRepositoryImpl(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public List<ConversationData> getConversationsForUser(Long userId) {
    return dsl.select(
            CONVERSATIONS.ID,
            multiset(
                    select(PARTICIPANTS.ID, USERS.FULL_NAME, USERS.PROFILE_IMAGE_ID)
                        .from(PARTICIPANTS)
                        .join(USERS)
                        .on(PARTICIPANTS.USER_ID.eq(USERS.ID))
                        .where(PARTICIPANTS.CONVERSATION_ID.eq(CONVERSATIONS.ID)))
                .convertFrom(r -> r.map(Records.mapping(ParticipantData::new))),
            row(
                    MESSAGES.ID,
                    CONVERSATIONS.ID,
                    MESSAGES.SENDER_ID,
                    MESSAGES.CONTENT,
                    MESSAGES.CREATED_AT)
                .mapping(MessageData::new))
        .from(CONVERSATIONS)
        .join(PARTICIPANTS)
        .on(CONVERSATIONS.ID.eq(PARTICIPANTS.CONVERSATION_ID))
        .join(USERS)
        .on(PARTICIPANTS.USER_ID.eq(USERS.ID))
        .join(MESSAGES)
        .on(MESSAGES.CONVERSATION_ID.eq(CONVERSATIONS.ID))
        .where(
            MESSAGES
                .ID
                .in(
                    select(MESSAGES.ID)
                        .from(MESSAGES)
                        .join(PARTICIPANTS)
                        .on(MESSAGES.CONVERSATION_ID.eq(PARTICIPANTS.CONVERSATION_ID))
                        .where(PARTICIPANTS.USER_ID.eq(userId))
                        .orderBy(MESSAGES.CREATED_AT.desc())
                        .limit(1))
                .and(PARTICIPANTS.USER_ID.eq(userId)))
        .fetchInto(ConversationData.class);
  }

  @Override
  public boolean existsByConversationIdAndUserId(Long conversationId, Long userId) {
    return dsl.selectOne()
        .from(PARTICIPANTS)
        .where(PARTICIPANTS.CONVERSATION_ID.eq(conversationId))
        .and(PARTICIPANTS.USER_ID.eq(userId))
        .fetchOptional()
        .isPresent();
  }
}
