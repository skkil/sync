package com.skkil.sync.user.dto.response;

import lombok.Builder;

@Builder
public record GetProfileResponse(Long userId, String name, String email, String bio) {}
