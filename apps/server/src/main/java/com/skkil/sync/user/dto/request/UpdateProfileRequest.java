package com.skkil.sync.user.dto.request;

import com.skkil.sync.user.constant.Handle;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(min = 1, max = 255) String name,
    @Size(min = Handle.MIN_LENGTH, max = Handle.MAX_LENGTH) String handle,
    Long profileImageId,
    Boolean isOnboarded,
    @Size(max = 1000) String bio,
    @Size(max = 255) String profession) {}
