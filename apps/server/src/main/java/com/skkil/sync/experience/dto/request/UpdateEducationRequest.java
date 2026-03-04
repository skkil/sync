package com.skkil.sync.experience.dto.request;

import com.skkil.sync.experience.constant.ExperienceType;
import com.skkil.sync.experience.constant.ExperienceVisibility;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateEducationRequest(
    @NotNull ExperienceType type,
    Long providerId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    ExperienceVisibility visibility,
    String major,
    BigDecimal gpa)
    implements UpdateExperienceRequest {}
