package com.skkil.sync.user.dto.response;

import com.skkil.sync.user.constant.Role;
import lombok.Builder;

@Builder
public record GetAuthenticatedUserResponse(
    Role role,
    Long userId,
    String fullName,
    String email,
    String profileImageUrl,
    boolean isOnboarded) {}
