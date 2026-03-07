package com.skkil.sync.provider.dto.response;

import com.skkil.sync.provider.constant.ProviderType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetProjectResponse(
    Long id,
    ProviderType type,
    String name,
    String description,
    String contactInfo,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long verifiedBy,
    boolean isMaintainer)
    implements GetProviderResponse {}
