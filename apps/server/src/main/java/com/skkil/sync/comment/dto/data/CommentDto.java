package com.skkil.sync.comment.dto.data;

import java.time.OffsetDateTime;

public record CommentDto(
    Long id,
    Long authorId,
    String authorHandle,
    String authorName,
    Long authorProfileImageId,
    String content,
    Boolean deleted,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
