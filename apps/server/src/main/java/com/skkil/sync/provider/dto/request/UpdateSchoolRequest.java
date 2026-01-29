package com.skkil.sync.provider.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.constant.SchoolType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateSchoolRequest(
    @NotNull ProviderType type, String name, String description, SchoolType schoolType)
    implements UpdateProviderRequest {}
