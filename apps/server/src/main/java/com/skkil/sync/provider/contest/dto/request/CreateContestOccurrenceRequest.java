package com.skkil.sync.provider.contest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateContestOccurrenceRequest(@NotBlank String title, String description) {}
