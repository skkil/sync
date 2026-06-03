package com.skkil.sync.reflection.dto.data;

import java.time.LocalDateTime;

public record ReflectionDto(
    Long id,
    String slug,
    Long authorId,
    String authorName,
    Long projectId,
    String projectName,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long likeCount,
    Long commentCount,
    Boolean bookmarked) {}
