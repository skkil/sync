package com.skkil.sync.project.dto.summary;

import lombok.Builder;

@Builder
public record ProjectSummary(
    String handle,
    String name,
    String description,
    String website,
    boolean isPublic,
    String iconUrl) {}
