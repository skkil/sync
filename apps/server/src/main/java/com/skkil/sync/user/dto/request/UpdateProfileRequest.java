package com.skkil.sync.user.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(@Size(min = 1, max = 255) String name) {}
