package com.skkil.sync.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

public record UpdateTagRequest(@NotBlank String name, @Nullable String description) {}
