package com.skkil.sync.experience.dto.request;

import com.skkil.sync.experience.constant.ExperienceType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateEmploymentRequest(
    @NotNull ExperienceType type,
    @NotNull Long providerId,
    @NotNull LocalDateTime startDate,
    LocalDateTime endDate)
    implements CreateExperienceRequest {}
