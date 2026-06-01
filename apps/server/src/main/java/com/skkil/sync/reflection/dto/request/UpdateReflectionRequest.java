package com.skkil.sync.reflection.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateReflectionRequest(Long projectId, @NotBlank String content) {}
