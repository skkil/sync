package com.skkil.sync.message.dto.data;

import java.time.LocalDateTime;

public record MessageData(
    Long id, Long conversationId, Long senderId, String content, LocalDateTime timestamp) {}
