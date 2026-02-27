package com.skkil.sync.message.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.message.dto.request.SendMessageRequest;
import com.skkil.sync.message.service.ConversationService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
public class MessageWebSocketController {

  private final ConversationService conversationService;

  public MessageWebSocketController(ConversationService conversationService) {
    this.conversationService = conversationService;
  }

  @MessageMapping("/conversations/{conversationId}/send")
  public void sendMessage(
      AuthenticatedUser user,
      @DestinationVariable Long conversationId,
      @Payload @Validated SendMessageRequest request) {
    conversationService.sendMessage(conversationId, user.userId(), request);
  }
}
