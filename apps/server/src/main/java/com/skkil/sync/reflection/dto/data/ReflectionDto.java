package com.skkil.sync.reflection.dto.data;

import java.time.LocalDateTime;

public record ReflectionDto(
    Long id,
    Long authorId,
    Long authorName,
    String content,
    Long projectId,
    String projectName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
