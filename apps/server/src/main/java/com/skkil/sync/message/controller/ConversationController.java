package com.skkil.sync.message.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.message.dto.response.CreateConversationResponse;
import com.skkil.sync.message.dto.response.GetConversationsResponse;
import com.skkil.sync.message.dto.response.GetMessagesResponse;
import com.skkil.sync.message.dto.response.GetUnreadMessagesCountResponse;
import com.skkil.sync.message.service.ConversationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConversationController {

  private final ConversationService conversationService;

  public ConversationController(ConversationService conversationService) {
    this.conversationService = conversationService;
  }

  @PostMapping("/conversations")
  public CreateConversationResponse createConversation(
      @AuthenticationPrincipal AuthenticatedUser user, @RequestParam Long participantId) {
    return conversationService.createConversation(user.userId(), participantId);
  }

  @GetMapping("/conversations")
  public GetConversationsResponse getConversations(
      @AuthenticationPrincipal AuthenticatedUser user) {
    return conversationService.getConversations(user.userId());
  }

  @GetMapping("/conversations/{conversationId}/messages")
  public GetMessagesResponse getMessages(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable Long conversationId,
      @RequestParam(required = false) Long after,
      @RequestParam(required = false, defaultValue = "100") Long size) {
    return conversationService.getMessages(user.userId(), conversationId, after, size);
  }

  @GetMapping("/conversations/unread")
  public GetUnreadMessagesCountResponse getUnreadMessagesCount(
      @AuthenticationPrincipal AuthenticatedUser user) {
    return conversationService.getUnreadMessagesCount(user.userId());
  }
}
