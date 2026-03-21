package com.skkil.sync.message.security;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.security.CustomPermissionEvaluator;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.common.security.enums.PermissionEvaluatorType;
import com.skkil.sync.message.repository.ConversationRepository;
import org.springframework.stereotype.Component;

@Component
public class ConversationPermissionEvaluator implements CustomPermissionEvaluator {

  private final ConversationRepository conversationRepository;

  public ConversationPermissionEvaluator(ConversationRepository conversationRepository) {
    this.conversationRepository = conversationRepository;
  }

  @Override
  public PermissionEvaluatorType type() {
    return PermissionEvaluatorType.CONVERSATION;
  }

  @Override
  public boolean hasPermission(
      AuthenticatedUser user, Long targetId, PermissionOperation permission) {
    return conversationRepository.existsByConversationIdAndUserId(targetId, user.userId());
  }
}
