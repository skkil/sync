package com.skkil.sync.provider.project.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import jakarta.validation.constraints.NotNull;

public record CreateProjectRequest(
    @NotNull ProviderType type, @NotNull String name, @NotNull String description)
    implements CreateProviderRequest {}
