package com.skkil.sync.reflection.dto.data;

import java.time.LocalDateTime;

public record ReflectionDto(
    Long id,
    String slug,
    Long authorId,
    String authorName,
    String content,
    Long projectId,
    String projectName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long likeCount,
    Long commentCount,
    Boolean bookmarked) {}
