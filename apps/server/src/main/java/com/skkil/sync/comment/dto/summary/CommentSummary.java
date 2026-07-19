package com.skkil.sync.comment.dto.summary;

import com.skkil.sync.user.dto.summary.UserSummary;
import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record CommentSummary(
    Long id,
    UserSummary author,
    boolean isPostAuthor,
    String content,
    boolean isDeleted,
    boolean isAccepted,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
