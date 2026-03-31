package com.skkil.sync.provider.dto.data;

import com.skkil.sync.provider.constant.ProviderType;
import java.time.Instant;

public record ProviderDto(
    ProviderType type,
    Long id,
    String name,
    Long verifiedByUserId,
    boolean isMaintainer,
    Instant createdAt,
    Instant updatedAt) {}
