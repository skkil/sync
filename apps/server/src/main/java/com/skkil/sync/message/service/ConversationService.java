package com.skkil.sync.message.service;

import com.skkil.sync.message.dto.request.SendMessageRequest;
import com.skkil.sync.message.dto.response.CreateConversationResponse;
import com.skkil.sync.message.dto.response.GetConversationsResponse;
import com.skkil.sync.message.dto.response.GetMessagesResponse;
import com.skkil.sync.message.dto.response.SendMessageResponse;
import com.skkil.sync.message.exception.MessageToSelfException;
import com.skkil.sync.message.model.Conversation;
import com.skkil.sync.message.model.Message;
import com.skkil.sync.message.model.Participant;
import com.skkil.sync.message.repository.ConversationRepository;
import com.skkil.sync.message.repository.MessageRepository;
import com.skkil.sync.user.service.UserService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ConversationService {

  private final ConversationRepository conversationRepository;
  private final MessageRepository messageRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final UserService userService;

  public ConversationService(
      ConversationRepository conversationRepository,
      MessageRepository messageRepository,
      SimpMessagingTemplate messagingTemplate,
      UserService userService) {
    this.conversationRepository = conversationRepository;
    this.messageRepository = messageRepository;
    this.messagingTemplate = messagingTemplate;
    this.userService = userService;
  }

  @Transactional
  public CreateConversationResponse createConversation(Long userId, Long participantId) {
    if (userId.equals(participantId)) {
      throw new MessageToSelfException();
    }

    var conversationOptional = conversationRepository.findByParticipants(userId, participantId);
    Conversation conversation;

    if (conversationOptional.isEmpty()) {
      log.debug(
          "No existing conversation between {} and {}, creating new one", userId, participantId);
      var newConversation = new Conversation();

      Participant
          participant1 =
              Participant.builder()
                  .user(userService.getUserReference(userId))
                  .conversation(newConversation)
                  .build(),
          participant2 =
              Participant.builder()
                  .user(userService.getUserReference(participantId))
                  .conversation(newConversation)
                  .build();

      newConversation.getParticipants().add(participant1);
      newConversation.getParticipants().add(participant2);
      conversation = conversationRepository.save(newConversation);
    } else {
      conversation = conversationOptional.get();
    }

    return new CreateConversationResponse(conversation.getId().toString());
  }

  @Transactional(readOnly = true)
  public GetConversationsResponse getConversations(Long userId) {
    var conversations =
        conversationRepository.findByUserId(userId).stream()
            .map(
                conversation -> {
                  var participants =
                      conversation.getParticipants().stream()
                          .map(
                              participant ->
                                  new GetConversationsResponse.Participant(
                                      participant.getUser().getId().toString(),
                                      participant.getUser().getFullName()))
                          .toList();

                  var lastMessage =
                      messageRepository
                          .findLastMessageByConversation(conversation.getId())
                          .map(
                              m ->
                                  GetConversationsResponse.Message.builder()
                                      .messageId(m.getId().toString())
                                      .senderId(m.getSender().getUser().getId().toString())
                                      .content(m.getContent())
                                      .sentAt(
                                          m.getCreatedAt()
                                              .atZone(ZoneId.systemDefault())
                                              .toLocalDateTime())
                                      .build())
                          .orElse(null);

                  return GetConversationsResponse.Conversation.builder()
                      .conversationId(conversation.getId().toString())
                      .participants(participants)
                      .lastMessage(lastMessage)
                      .build();
                })
            .toList();

    return new GetConversationsResponse(conversations);
  }

  @Transactional(readOnly = true)
  public GetMessagesResponse getMessages(Long userId, Long conversationId, Long after, Long size) {
    Pageable pageable = Pageable.ofSize(size.intValue());
    var messages =
        messageRepository
            .getMessages(conversationRepository.getReferenceById(conversationId), after, pageable)
            .map(
                message ->
                    GetMessagesResponse.Message.builder()
                        .messageId(message.getId().toString())
                        .senderId(message.getSender().getUser().getId().toString())
                        .content(message.getContent())
                        .sentAt(
                            LocalDateTime.ofInstant(message.getCreatedAt(), ZoneId.systemDefault()))
                        .build());

    return new GetMessagesResponse(messages);
  }

  @Transactional
  public void sendMessage(Long conversationId, Long senderId, SendMessageRequest request) {
    Conversation conversation = conversationRepository.findById(conversationId).orElseThrow();

    Participant sender =
        conversation.getParticipants().stream()
            .filter(participant -> participant.getUser().getId().equals(senderId))
            .findFirst()
            .orElseThrow();

    Message message =
        Message.builder()
            .conversation(conversation)
            .sender(sender)
            .content(request.content())
            .build();
    messageRepository.save(message);

    for (Participant participant : conversation.getParticipants()) {
      log.debug(
          "Sending message {} to participant {}", message.getId(), participant.getUser().getId());

      messagingTemplate.convertAndSendToUser(
          participant.getUser().getId().toString(),
          "queue/messages",
          SendMessageResponse.builder()
              .conversationId(conversation.getId().toString())
              .messageId(message.getId().toString())
              .senderId(sender.getUser().getId().toString())
              .content(message.getContent())
              .sentAt(LocalDateTime.ofInstant(message.getCreatedAt(), ZoneId.systemDefault()))
              .build());
    }
  }
}
