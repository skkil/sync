package com.skkil.sync.message.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.message.dto.request.SendMessageRequest;
import com.skkil.sync.message.service.ConversationService;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
public class MessageWebSocketController {

  private final ConversationService conversationService;

  public MessageWebSocketController(ConversationService conversationService) {
    this.conversationService = conversationService;
  }

  @MessageMapping("/conversations/send")
  public void sendMessage(Principal principal, @Payload @Validated SendMessageRequest request) {
    AuthenticatedUser user = (AuthenticatedUser) ((Authentication) principal).getPrincipal();
    conversationService.sendMessage(user.userId(), request);
  }
}
