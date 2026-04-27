package com.skkil.sync.reflection.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateReflectionRequest(String title, @NotBlank String content) {}
