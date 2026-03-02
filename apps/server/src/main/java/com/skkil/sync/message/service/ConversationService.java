package com.skkil.sync.message.service;

import com.skkil.sync.media.service.MediaService;
import com.skkil.sync.message.dto.request.SendMessageRequest;
import com.skkil.sync.message.dto.response.CreateConversationResponse;
import com.skkil.sync.message.dto.response.GetConversationsResponse;
import com.skkil.sync.message.dto.response.GetMessagesResponse;
import com.skkil.sync.message.dto.response.GetUnreadMessagesCountResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ConversationService {

  private final ConversationRepository conversationRepository;
  private final MessageRepository messageRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final UserService userService;
  private final MediaService mediaService;

  public ConversationService(
      ConversationRepository conversationRepository,
      MessageRepository messageRepository,
      SimpMessagingTemplate messagingTemplate,
      UserService userService,
      MediaService mediaService) {
    this.conversationRepository = conversationRepository;
    this.messageRepository = messageRepository;
    this.messagingTemplate = messagingTemplate;
    this.userService = userService;
    this.mediaService = mediaService;
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
    var conversations = conversationRepository.getConversationsForUser(userId);

    return new GetConversationsResponse(
        conversations.stream()
            .map(
                conversation -> {
                  var participants =
                      conversation.participants().stream()
                          .map(
                              participant ->
                                  GetConversationsResponse.Participant.builder()
                                      .userId(participant.id().toString())
                                      .name(participant.name())
                                      .profileImageUrl(
                                          participant.profileImageId() == null
                                              ? null
                                              : mediaService
                                                  .getMediaUrl(participant.profileImageId())
                                                  .toString())
                                      .build())
                          .toList();

                  var lastMessage =
                      conversation.lastMessage() == null
                          ? null
                          : GetConversationsResponse.Message.builder()
                              .messageId(conversation.lastMessage().id().toString())
                              .senderId(conversation.lastMessage().senderId().toString())
                              .content(conversation.lastMessage().content())
                              .sentAt(conversation.lastMessage().timestamp())
                              .build();

                  return GetConversationsResponse.Conversation.builder()
                      .conversationId(conversation.conversationId().toString())
                      .participants(participants)
                      .lastMessage(lastMessage)
                      .build();
                })
            .toList());
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasPermission(#conversationId, 'CONVERSATION', 'READ')")
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
  @PreAuthorize("hasPermission(#conversationId, 'CONVERSATION', 'EDIT')")
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

  @Transactional(readOnly = true)
  public GetUnreadMessagesCountResponse getUnreadMessagesCount(Long userId) {
    var conversations = messageRepository.getUnreadMessagesCount(userId);
    return new GetUnreadMessagesCountResponse(
        conversations.stream()
            .map(
                conversation ->
                    new GetUnreadMessagesCountResponse.Conversation(
                        conversation.conversationId().toString(),
                        conversation.unreadMessageCount()))
            .toList());
  }
}
