package com.skkil.sync.recommendation.dto.data;

import java.time.LocalDateTime;

public record FeedDto(
    Long id,
    Long authorId,
    String authorHandle,
    String authorName,
    Long authorProfileImageId,
    String content,
    Long likeCount,
    Long commentCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
