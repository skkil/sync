package com.skkil.sync.message.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "messages")
@Getter
public class Message extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private Participant sender;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  protected Message() {}

  @Builder
  public Message(Conversation conversation, Participant sender, String content) {
    this.conversation = conversation;
    this.sender = sender;
    this.content = content;
  }
}
