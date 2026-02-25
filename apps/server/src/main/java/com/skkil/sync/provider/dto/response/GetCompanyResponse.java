package com.skkil.sync.provider.dto.response;

import com.skkil.sync.provider.constant.ProviderType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetCompanyResponse(
    ProviderType type,
    Long id,
    String name,
    String description,
    String contactInfo,
    String industry,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long verifiedBy,
    boolean isMaintainer)
    implements GetProviderResponse {}
