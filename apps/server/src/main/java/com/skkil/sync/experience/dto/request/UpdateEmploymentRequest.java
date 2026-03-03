package com.skkil.sync.experience.dto.request;

import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.constant.ExperienceVisibility;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UpdateEmploymentRequest(
    @NotNull ExperienceType type,
    Long providerId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    ExperienceVisibility visibility)
    implements UpdateExperienceRequest {}
