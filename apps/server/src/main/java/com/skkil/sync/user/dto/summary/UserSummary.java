package com.skkil.sync.user.dto.summary;

import lombok.Builder;

@Builder
public record UserSummary(String handle, String name, String profileImageUrl) {}
