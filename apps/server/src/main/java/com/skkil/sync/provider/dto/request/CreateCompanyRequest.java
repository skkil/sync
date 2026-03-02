package com.skkil.sync.provider.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import jakarta.validation.constraints.NotNull;

public record CreateCompanyRequest(
    @NotNull ProviderType type, @NotNull String name, @NotNull String description, String industry)
    implements CreateProviderRequest {}
