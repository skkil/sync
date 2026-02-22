package com.skkil.sync.user.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(min = 1, max = 255) String name,
    Long profileImageId,
    Boolean isOnboarded,
    @Size(max = 1000) String bio,
    @Size(max = 255) String profession) {}
