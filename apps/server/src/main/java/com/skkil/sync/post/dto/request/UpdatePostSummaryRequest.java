package com.skkil.sync.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostSummaryRequest(@NotBlank String summary) {}
