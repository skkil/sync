package com.skkil.sync.provider.project.dto.data;

import java.time.LocalDateTime;

public record TeamBuildingPostDto(
    Long id,
    Long projectId,
    String projectName,
    String projectDescription,
    String title,
    String content,
    Long likeCount,
    Long commentCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
