package com.skkil.sync.post.dto.data;

import com.skkil.sync.post.model.PostType;
import java.time.LocalDateTime;
import org.jspecify.annotations.Nullable;

public record PostDto(
    Long id,
    PostType type,
    String slug,
    Long authorId,
    String authorName,
    String authorHandle,
    @Nullable Long projectId,
    @Nullable String projectName,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long likeCount,
    Long commentCount,
    Boolean bookmarked) {}
