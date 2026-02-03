package com.skkil.sync.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(@NotNull Long to, @NotBlank @Size(max = 10000) String content) {}
