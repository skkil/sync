package com.skkil.sync.post.dto.data;

import com.skkil.sync.post.model.PostType;
import java.time.LocalDateTime;

public record PostDto(
    Long id,
    PostType type,
    String slug,
    Long authorId,
    String authorName,
    String authorHandle,
    Long projectId,
    String projectName,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long likeCount,
    Long commentCount,
    Boolean bookmarked) {}
