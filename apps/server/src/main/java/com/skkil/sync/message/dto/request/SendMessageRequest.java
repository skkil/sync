package com.skkil.sync.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(@NotNull Long to, @NotBlank String content) {}
