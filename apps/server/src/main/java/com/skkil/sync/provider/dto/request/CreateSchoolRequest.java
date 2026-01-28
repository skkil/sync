package com.skkil.sync.provider.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.constant.SchoolType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateSchoolRequest(
    @NotNull ProviderType type,
    @NotNull String name,
    @NotNull String description,
    @NotNull SchoolType schoolType)
    implements CreateProviderRequest {}
