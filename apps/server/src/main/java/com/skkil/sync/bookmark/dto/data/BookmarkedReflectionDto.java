package com.skkil.sync.bookmark.dto.data;

import java.time.LocalDateTime;

public record BookmarkedReflectionDto(
    Long id,
    String slug,
    Long bookmarkId,
    LocalDateTime bookmarkedAt,
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
