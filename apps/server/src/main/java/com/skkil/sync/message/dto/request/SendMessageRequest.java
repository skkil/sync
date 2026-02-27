package com.skkil.sync.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(@NotBlank @Size(max = 10000) String content) {}
