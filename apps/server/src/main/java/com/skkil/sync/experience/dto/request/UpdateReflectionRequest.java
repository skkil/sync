package com.skkil.sync.experience.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateReflectionRequest(@NotBlank String content) {}
