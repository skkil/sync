package com.skkil.sync.message.repository;

import com.skkil.sync.message.model.Conversation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConversationRepository
    extends JpaRepository<Conversation, Long>, CustomConversationRepository {

  @Override
  @EntityGraph(attributePaths = {"participants"})
  Optional<Conversation> findById(Long conversationId);

  @Query(
      """
      SELECT c FROM Conversation c
      LEFT JOIN FETCH c.participants p
      WHERE p.user.id = :userId
      """)
  List<Conversation> findByUserId(Long userId);

  @Query(
      """
      SELECT c FROM Conversation c
      JOIN c.participants p1
      JOIN c.participants p2
      WHERE (p1.user.id = :user1 AND p2.user.id = :user2)
      OR (p1.user.id = :user2 AND p2.user.id = :user1)
      """)
  Optional<Conversation> findByParticipants(Long user1, Long user2);
}
