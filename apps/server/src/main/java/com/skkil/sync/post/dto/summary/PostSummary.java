package com.skkil.sync.post.dto.summary;

import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.project.dto.summary.ProjectSummary;
import com.skkil.sync.user.dto.summary.UserSummary;
import java.time.OffsetDateTime;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record PostSummary(
    Long id,
    String slug,
    @Nullable String title,
    PostType type,
    PostStatus status,
    UserSummary author,
    @Nullable ProjectSummary project,
    boolean resolved,
    boolean isAuthor,
    OffsetDateTime createdAt,
    Long likeCount,
    boolean liked,
    Long commentCount,
    boolean bookmarked,
    @Nullable OffsetDateTime bookmarkedAt,
    @Nullable OffsetDateTime likedAt) {}
