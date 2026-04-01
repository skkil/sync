package com.skkil.sync.provider.dto.query;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.enums.ProviderVerificationStatus;
import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record ProviderQuery(
    String query,
    Instant createdAfter,
    Long afterId,
    List<ProviderType> types,
    List<ProviderVerificationStatus> verificationStatuses,
    Long maintainerId) {}
