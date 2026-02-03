package com.skkil.sync.lab.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLabRequest(
    @NotBlank(message = "Lab name cannot be blank") String name,
    String description,
    String oneLineReview,
    @NotNull(message = "Professor ID cannot be null") Long professorId,
    @NotNull(message = "School ID cannot be null") Long schoolId,
    String researchArea,
    String detailedResearchField,
    String contactInfo) {}
