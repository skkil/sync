package com.skkil.sync.reflection.dto.data;

import java.time.LocalDateTime;

public record ReflectionDto(
    Long id,
    Long authorId,
    String authorName,
    String content,
    Long projectId,
    String projectName,
    Long likeCount,
    Long commentCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
