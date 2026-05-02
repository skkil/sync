package com.skkil.sync.provider.project.dto.data;

import java.time.LocalDateTime;

public record ProjectDto(
    Long id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {}
