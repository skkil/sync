package com.skkil.sync.post.dto.summary;

import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.model.PostScope;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.project.dto.summary.ProjectSummary;
import com.skkil.sync.user.dto.summary.UserSummary;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record PostSummary(
    Long id,
    String slug,
    @Nullable String title,
    PostType type,
    PostStatus status,
    PostScope scope,
    UserSummary author,
    @Nullable ProjectSummary project,
    boolean resolved,
    boolean isAuthor,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    Long likeCount,
    boolean liked,
    Long commentCount,
    boolean bookmarked,
    List<TagSummary> tags,
    String preview,
    List<GetPostResponse.Media> previewMedia,
    int mediaCount,
    int wordCount) {}
