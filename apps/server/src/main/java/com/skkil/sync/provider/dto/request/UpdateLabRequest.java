package com.skkil.sync.provider.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateLabRequest(
    @NotNull ProviderType type,
    String name,
    String description,
    String oneLineReview,
    String researchArea,
    String detailedResearchField)
    implements UpdateProviderRequest {}
