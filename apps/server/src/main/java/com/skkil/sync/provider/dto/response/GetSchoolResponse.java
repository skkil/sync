package com.skkil.sync.provider.dto.response;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.constant.SchoolType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetSchoolResponse(
    Long id,
    ProviderType type,
    String name,
    String description,
    SchoolType schoolType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long verifiedBy)
    implements GetProviderResponse {}
