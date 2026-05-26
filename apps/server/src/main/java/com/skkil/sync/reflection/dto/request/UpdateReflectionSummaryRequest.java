package com.skkil.sync.reflection.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateReflectionSummaryRequest(@NotBlank String summary) {}
