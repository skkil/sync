package com.skkil.sync.message.repository;

import com.skkil.sync.message.model.Conversation;
import com.skkil.sync.message.model.Message;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {

  @Query(
      """
      SELECT m FROM Message m
      JOIN FETCH m.sender
      WHERE m.conversation = :conversation
      AND (:after IS NULL OR m.id > :after)
      ORDER BY m.createdAt DESC
      """)
  public Page<Message> getMessages(Conversation conversation, Long after, Pageable pageable);

  @Query(
      """
      SELECT m FROM Message m
      JOIN FETCH m.sender
      WHERE m.conversation.id = :conversationId
      ORDER BY m.createdAt DESC
      LIMIT 1
      """)
  public Optional<Message> findLastMessageByConversation(Long conversationId);
}
