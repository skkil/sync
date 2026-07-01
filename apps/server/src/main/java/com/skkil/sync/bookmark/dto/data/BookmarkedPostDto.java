package com.skkil.sync.bookmark.dto.data;

import java.time.OffsetDateTime;

public record BookmarkedPostDto(
    Long id,
    String slug,
    Long bookmarkId,
    OffsetDateTime bookmarkedAt,
    Long authorId,
    String authorHandle,
    String authorName,
    Long authorProfileImageId,
    String content,
    Long likeCount,
    Long commentCount,
    Boolean bookmarked,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
