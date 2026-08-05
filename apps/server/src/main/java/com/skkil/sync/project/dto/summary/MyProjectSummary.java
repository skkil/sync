package com.skkil.sync.project.dto.summary;

import com.skkil.sync.project.model.JoinPolicy;
import com.skkil.sync.project.model.Role;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record MyProjectSummary(
    String handle,
    String name,
    @Nullable String description,
    @Nullable String website,
    boolean isPublic,
    JoinPolicy joinPolicy,
    long followerCount,
    @Nullable String iconUrl,
    Role role,
    boolean isOwner,
    long memberCount,
    long unresolvedQuestionCount) {}
