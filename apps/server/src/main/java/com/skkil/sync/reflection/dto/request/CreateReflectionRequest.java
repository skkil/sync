package com.skkil.sync.reflection.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateReflectionRequest(Long experienceId, @NotBlank String content) {}
