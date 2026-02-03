package com.skkil.sync.experience.dto.request;

import com.skkil.sync.experience.constant.ExperienceType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateEducationRequest(
    @NotNull ExperienceType type,
    @NotNull Long providerId,
    @NotNull LocalDateTime startDate,
    LocalDateTime endDate,
    String major,
    BigDecimal gpa)
    implements CreateExperienceRequest {}
