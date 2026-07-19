package com.skkil.sync.post.dto.summary;

import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record TagSummary(
    Long id,
    String name,
    @Nullable String description,
    Long postCount,
    Long followerCount,
    @Nullable String projectHandle,
    boolean isFollowing) {}
