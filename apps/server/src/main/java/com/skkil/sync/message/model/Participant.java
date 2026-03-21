package com.skkil.sync.message.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(
    name = "participants",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "conversation_id"})})
@Getter
public class Participant extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @Column(name = "last_read_at")
  private Instant lastReadAt;

  protected Participant() {}

  @Builder
  public Participant(User user, Conversation conversation) {
    this.user = user;
    this.conversation = conversation;
  }

  public void setConversation(Conversation conversation) {
    this.conversation = conversation;
  }
}
