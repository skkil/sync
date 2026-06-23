package com.skkil.sync.post.dto.data;

import java.time.LocalDateTime;
import org.jspecify.annotations.Nullable;

public record PostDto(
    Long id,
    String slug,
    Long authorId,
    String authorName,
    @Nullable Long projectId,
    @Nullable String projectName,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long likeCount,
    Long commentCount,
    Boolean bookmarked) {}
