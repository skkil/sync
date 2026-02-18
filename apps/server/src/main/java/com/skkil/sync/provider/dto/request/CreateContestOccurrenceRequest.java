package com.skkil.sync.provider.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateContestOccurrenceRequest(@NotBlank String title, String description) {}
