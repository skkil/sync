package com.skkil.sync.recommendation.dto.data;

import java.time.LocalDateTime;

public record FeedDto(
    Long id,
    String slug,
    Long authorId,
    String authorHandle,
    String authorName,
    Long authorProfileImageId,
    String content,
    Long likeCount,
    Long commentCount,
    Boolean bookmarked,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
