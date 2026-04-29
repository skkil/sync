package com.skkil.sync.reflection.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReflectionRequest(String title, @Valid @NotNull Content content) {

  public static record Content(@NotBlank String text, @NotBlank String json) {}
}
