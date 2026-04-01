package com.skkil.sync.provider.dto.data;

import com.skkil.sync.provider.constant.ProviderType;
import java.time.LocalDateTime;

public record ProviderDto(
    ProviderType type,
    Long id,
    String name,
    Long verifiedByUserId,
    boolean isMaintainer,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
