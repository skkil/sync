package com.skkil.sync.user.dto.response;

import lombok.Builder;

@Builder
public record GetProfileResponse(
    String userId,
    String name,
    String email,
    String bio,
    String profession,
    String profileImageUrl,
    boolean isFollowing) {}
