package com.skkil.sync.recommendation.dto.data;

import java.time.OffsetDateTime;

public record FeedDto(
    Long id,
    String slug,
    Long authorId,
    String authorHandle,
    String authorName,
    Long authorProfileImageId,
    String content,
    String projectHandle,
    String projectName,
    Long likeCount,
    Long commentCount,
    Boolean bookmarked,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
