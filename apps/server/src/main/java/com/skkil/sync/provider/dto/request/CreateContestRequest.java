package com.skkil.sync.provider.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateContestRequest(
    @NotNull ProviderType type, @NotBlank String name, String description, Long hostProviderId)
    implements CreateProviderRequest {}
