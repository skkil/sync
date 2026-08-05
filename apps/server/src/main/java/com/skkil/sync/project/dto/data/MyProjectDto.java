package com.skkil.sync.project.dto.data;

import com.skkil.sync.project.model.JoinPolicy;
import com.skkil.sync.project.model.Role;
import org.jspecify.annotations.Nullable;

public record MyProjectDto(
    Long id,
    String handle,
    String name,
    @Nullable String description,
    @Nullable String website,
    Boolean isPublic,
    JoinPolicy joinPolicy,
    Long followerCount,
    @Nullable Long iconMediaId,
    Role role,
    Boolean isOwner,
    Long memberCount,
    Long unresolvedQuestionCount) {}
