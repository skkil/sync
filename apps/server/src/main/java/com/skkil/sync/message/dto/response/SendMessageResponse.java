package com.skkil.sync.message.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SendMessageResponse(
    String conversationId,
    String messageId,
    String senderId,
    String content,
    LocalDateTime sentAt) {}
