package com.skkil.sync.user.dto.response;

import lombok.Builder;

@Builder
public record GetAuthenticatedUserResponse(
    Long userId, String fullName, String email, String profileImageUrl, boolean isOnboarded) {}
