package com.skkil.sync.message.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.message.dto.response.GetConversationsResponse;
import com.skkil.sync.message.dto.response.GetMessagesResponse;
import com.skkil.sync.message.service.ConversationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConversationController {

  private final ConversationService conversationService;

  public ConversationController(ConversationService conversationService) {
    this.conversationService = conversationService;
  }

  @GetMapping("/conversations")
  public GetConversationsResponse getConversations(
      @AuthenticationPrincipal AuthenticatedUser user) {
    return conversationService.getConversations(user.userId());
  }

  @GetMapping("/conversations/messages")
  public GetMessagesResponse getMessages(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(required = true) Long participantId,
      @RequestParam(required = false) Long after,
      @RequestParam(required = false, defaultValue = "100") Long size) {
    return conversationService.getMessages(user.userId(), participantId, after, size);
  }
}
