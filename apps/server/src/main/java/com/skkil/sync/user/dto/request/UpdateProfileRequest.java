package com.skkil.sync.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(min = 1, max = 255) String name,
    Long profileImageId,
    Boolean isOnboarded,
    @Max(1000) String bio,
    @Max(255) String profession) {}
